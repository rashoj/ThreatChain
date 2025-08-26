package com.honeynet.backend.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.honeynet.backend.DTO.IndicatorDTO;
import com.honeynet.backend.DTO.ThreatAnalysis;
import com.honeynet.backend.DTO.ThreatSummaryDTO;
import com.honeynet.backend.config.AlienVaultConfig;
import com.honeynet.backend.entity.*;
import com.honeynet.backend.model.BlockchainLogRepository;
import com.honeynet.backend.model.ThreatAnalysisRepo;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import com.honeynet.backend.entity.DemoBlockchainLog;
import com.honeynet.backend.model.DemoBlockchainLogRepository;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class AlienVaultService {

    private final AlienVaultConfig config;

    @Autowired
    private ObjectMapper objectMapper;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private ThreatAnalysisRepo threatAnalysisRepo;

    @Autowired
    private BlockchainLogRepository blockchainLogRepository;

    @Autowired
    private BlockchainLogService blockchainLogService;

    @Autowired
    private OpenAIService openAIService;

    @Autowired
    private DemoBlockchainLogRepository demoBlockchainLogRepository;


    @Autowired
    public AlienVaultService(AlienVaultConfig config) {
        this.config = config;
    }

    /**
     * Retrieves all distinct countries from stored threats.
     */
    public List<String> getAllCountries() {
        return threatAnalysisRepo.findDistinctCountries();
    }

    /**
     * Fetches subscribed pulses from AlienVault API, parses, enriches with AI summary,
     * stores in DB, and logs blockchain events. Skips duplicates based on name and revision.
     *
     * @return List of ThreatAnalysis DTOs representing the fetched pulses.
     */
    @Transactional
    public List<ThreatAnalysis> fetchAndParsePulses() {
        String url = "https://otx.alienvault.com/api/v1/pulses/subscribed";
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + config.getApiKey());

        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

        try {
            JsonNode root = objectMapper.readTree(response.getBody());
            JsonNode results = root.path("results");

            List<ThreatAnalysis> pulseList = objectMapper.readerForListOf(ThreatAnalysis.class).readValue(results);

            for (ThreatAnalysis dto : pulseList) {
                Optional<ThreatAnalysisEntity> optionalEntity = threatAnalysisRepo.findByNameAndRevision(dto.getName(), dto.getRevision());

                if (optionalEntity.isPresent()) {
                    updateExistingThreat(optionalEntity.get(), dto);
                } else {
                    insertNewThreat(dto);
                }
            }
            return pulseList;

        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    /**
     * Updates an existing ThreatAnalysisEntity with new DTO data and AI summary if missing.
     */
    @Transactional
    protected void updateExistingThreat(ThreatAnalysisEntity entityObj, ThreatAnalysis dto) {
        System.out.println("⚠️ Updating existing: " + dto.getName() + " | revision " + dto.getRevision());

        if (entityObj.getAiSummary() == null || entityObj.getAiSummary().isEmpty()) {
            String aiSummary = generateAiSummary(dto);
            System.out.println("🧠 Generated AI Summary for " + dto.getName() + ": " + aiSummary);
            entityObj.setAiSummary(aiSummary);
        }

        entityObj.setDescription(dto.getDescription());
        entityObj.setAuthorName(dto.getAuthorName());
        entityObj.setCreated(dto.getCreated());
        entityObj.setModified(dto.getModified());
        entityObj.setRevision(dto.getRevision());
        entityObj.setTlp(dto.getTlp());
        entityObj.setAdversary(dto.getAdversary());
        entityObj.setMoreIndicators(dto.isMoreIndicators());
        entityObj.setAttackIds(dto.getAttackIds());
        entityObj.setReferenceLinks(dto.getReferences());
        entityObj.setExtraSource(dto.getExtraSource());

        // Update tags
        entityObj.getTags().clear();
        List<ThreatTagEntity> tagEntities = dto.getTags().stream()
                .map(tagStr -> {
                    ThreatTagEntity tagEntity = new ThreatTagEntity();
                    tagEntity.setTag(tagStr);
                    tagEntity.setThreatAnalysis(entityObj);
                    return tagEntity;
                }).collect(Collectors.toList());
        entityObj.getTags().addAll(tagEntities);

        entityObj.setMalwareFamilies(new HashSet<>(dto.getMalwareFamilies()));
        entityObj.setIndustries(new HashSet<>(dto.getIndustries()));
        entityObj.setTargetedCountries(new HashSet<>(dto.getTargetedCountries()));

        // Update indicators
        entityObj.getIndicators().clear();
        List<IndicatorEntity> indicators = Optional.ofNullable(dto.getIndicators())
                .orElse(Collections.emptyList())
                .stream()
                .map(this::mapIndicatorDTOToEntity)
                .peek(i -> i.setThreatAnalysis(entityObj))
                .collect(Collectors.toList());
        entityObj.getIndicators().addAll(indicators);

        threatAnalysisRepo.save(entityObj);
    }

    /**
     * Inserts a new ThreatAnalysisEntity with AI summary and logs a blockchain event.
     */
    @Transactional
    protected void insertNewThreat(ThreatAnalysis dto) {
        System.out.println("✅ Inserting new: " + dto.getName() + " | revision " + dto.getRevision());

        ThreatAnalysisEntity entity = toEntity(dto);
        String aiSummary = generateAiSummary(dto);
        entity.setAiSummary(aiSummary);

        // FIX: save the 'entity' variable, not 'unsavedEntity' which was undefined
//        ThreatAnalysisEntity savedThreat = saveThreatEntity(entity);

//        BlockchainLog log = new BlockchainLog();
//        log.setEvent("Threat logged: " + dto.getName());
//        log.setBlockNumber(String.valueOf(System.currentTimeMillis()));
//        log.setTransactionHash("0x" + UUID.randomUUID().toString().replace("-", "").substring(0, 10));
//        log.setTimestamp(LocalDateTime.now());
//        log.setThreat(savedThreat);
//
//        saveBlockchainLog(log);
    }

//    @Transactional
//    public ThreatAnalysisEntity saveThreatEntity(ThreatAnalysisEntity entity) {
//        ThreatAnalysisEntity saved = threatAnalysisRepo.save(entity);
//        entityManager.flush();
//        System.out.println("Flushed ThreatAnalysisEntity with ID: " + saved.getId());
//        return saved;
//    }

//    @Transactional
//    public void saveBlockchainLog(BlockchainLog log) {
//        blockchainLogRepository.save(log);
//        entityManager.flush();
//        System.out.println("Flushed BlockchainLog for threat ID: " + (log.getThreat() != null ? log.getThreat().getId() : "null"));
//    }
@Transactional
public void saveDemoBlockchainLogForThreat(ThreatAnalysisEntity entity) {
    // Make sure entity is saved and has an ID
    if (entity.getId() == null) {
        entity = threatAnalysisRepo.save(entity);
    }

    DemoBlockchainLog log = new DemoBlockchainLog();
    log.setTransactionHash("demo-txhash-" + entity.getId());
    log.setAction("DEMO_LOGGED");
    log.setTimestamp(LocalDateTime.now());  // Pass LocalDateTime here
    log.setDetails("This is a demo blockchain log entry for threat " + entity.getId());
    log.setHash("somehashvalue");
    log.setPreviousHash("previoushashvalue");
    log.setThreat(entity);

    demoBlockchainLogRepository.save(log);
}


    private IndicatorEntity mapIndicatorDTOToEntity(IndicatorDTO indicatorDTO) {
        IndicatorEntity indicatorEntity = new IndicatorEntity();
        indicatorEntity.setIndicator(indicatorDTO.getIndicator());
        indicatorEntity.setType(indicatorDTO.getType());
        indicatorEntity.setCreated(indicatorDTO.getCreated());
        indicatorEntity.setContent(indicatorDTO.getContent());
        indicatorEntity.setTitle(indicatorDTO.getTitle());
        indicatorEntity.setDescription(indicatorDTO.getDescription());
        indicatorEntity.setExpiration(indicatorDTO.getExpiration());
        indicatorEntity.setActive(indicatorDTO.isActive());
        indicatorEntity.setRole(indicatorDTO.getRole());
        return indicatorEntity;
    }

    /**
     * Converts ThreatAnalysis DTO to ThreatAnalysisEntity including indicators and tags.
     */
    private ThreatAnalysisEntity toEntity(ThreatAnalysis dto) {
        ThreatAnalysisEntity entity = new ThreatAnalysisEntity();
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setAuthorName(dto.getAuthorName());
        entity.setCreated(dto.getCreated());
        entity.setModified(dto.getModified());
        entity.setRevision(dto.getRevision());
        entity.setTlp(dto.getTlp());
        entity.setAdversary(dto.getAdversary());
        entity.setMoreIndicators(dto.isMoreIndicators());

        List<ThreatTagEntity> tagEntities = dto.getTags().stream()
                .map(tagStr -> {
                    ThreatTagEntity tagEntity = new ThreatTagEntity();
                    tagEntity.setTag(tagStr);
                    tagEntity.setThreatAnalysis(entity);
                    return tagEntity;
                }).collect(Collectors.toList());
        entity.setTags(tagEntities);

        entity.setAttackIds(dto.getAttackIds());
        entity.setReferenceLinks(dto.getReferences());
        entity.setExtraSource(dto.getExtraSource());
        entity.setMalwareFamilies(new HashSet<>(dto.getMalwareFamilies()));
        entity.setIndustries(new HashSet<>(dto.getIndustries()));
        entity.setTargetedCountries(new HashSet<>(dto.getTargetedCountries()));

        entity.getIndicators().clear();
        List<IndicatorEntity> indicators = Optional.ofNullable(dto.getIndicators())
                .orElse(Collections.emptyList())
                .stream()
                .map(this::mapIndicatorDTOToEntity)
                .peek(i -> i.setThreatAnalysis(entity))
                .collect(Collectors.toList());

        entity.getIndicators().addAll(indicators);
        return entity;
    }

    /**
     * Converts ThreatAnalysisEntity to ThreatAnalysis DTO including indicators and tags.
     */
    private ThreatAnalysis toDTO(ThreatAnalysisEntity entity) {
        ThreatAnalysis dto = new ThreatAnalysis();
        dto.setId(String.valueOf(entity.getId()));
        dto.setName(entity.getName());
        dto.setDescription(entity.getDescription());
        dto.setAuthorName(entity.getAuthorName());
        dto.setCreated(entity.getCreated());
        dto.setModified(entity.getModified());
        dto.setRevision(entity.getRevision());
        dto.setTlp(entity.getTlp());
        dto.setAdversary(entity.getAdversary());
        dto.setMoreIndicators(entity.isMoreIndicators());
        dto.setAiSummary(entity.getAiSummary());

        List<String> tags = Optional.ofNullable(entity.getTags())
                .orElse(Collections.emptyList())
                .stream()
                .map(ThreatTagEntity::getTag)
                .collect(Collectors.toList());
        dto.setTags(tags);

        dto.setTargetedCountries(new ArrayList<>(Optional.ofNullable(entity.getTargetedCountries()).orElse(Collections.emptySet())));
        dto.setMalwareFamilies(new ArrayList<>(Optional.ofNullable(entity.getMalwareFamilies()).orElse(Collections.emptySet())));
        dto.setAttackIds(entity.getAttackIds());
        dto.setReferences(entity.getReferenceLinks());
        dto.setIndustries(new ArrayList<>(Optional.ofNullable(entity.getIndustries()).orElse(Collections.emptySet())));
        dto.setExtraSource(entity.getExtraSource());

        List<IndicatorDTO> indicators = Optional.ofNullable(entity.getIndicators())
                .orElse(Collections.emptyList())
                .stream()
                .map(indicator -> {
                    IndicatorDTO dtoIndicator = new IndicatorDTO();
                    dtoIndicator.setIndicator(indicator.getIndicator());
                    dtoIndicator.setType(indicator.getType());
                    dtoIndicator.setCreated(indicator.getCreated());
                    dtoIndicator.setContent(indicator.getContent());
                    dtoIndicator.setTitle(indicator.getTitle());
                    dtoIndicator.setDescription(indicator.getDescription());
                    dtoIndicator.setExpiration(indicator.getExpiration());
                    dtoIndicator.setActive(Boolean.TRUE.equals(indicator.getActive()));
                    dtoIndicator.setRole(indicator.getRole());
                    return dtoIndicator;
                }).collect(Collectors.toList());

        dto.setIndicators(indicators);
        return dto;
    }

    /**
     * Retrieves ThreatAnalysis DTO by ID.
     */
    public ThreatAnalysis getThreatById(Long id) {
        return threatAnalysisRepo.findById(id)
                .map(this::toDTO)
                .orElse(null);
    }

    /**
     * Retrieves all stored ThreatAnalysis DTOs.
     */
    public List<ThreatAnalysis> getAllStoredThreats() {
        return threatAnalysisRepo.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Returns paginated and sorted threat summaries with flexible sorting options.
     */
    public Page<ThreatSummaryDTO> getPaginatedThreatSummaries(int page, int size, String sortBy, String sortOrder) {
        ThreatSortField sortField = ThreatSortField.fromString(sortBy);

        Sort.Direction direction = "desc".equalsIgnoreCase(sortOrder) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Sort sort = Sort.by(direction, sortField.getField());
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<ThreatAnalysisEntity> entityPage = threatAnalysisRepo.findAll(pageable);

        List<ThreatSummaryDTO> dtos = entityPage.getContent().stream()
                .map(this::toSummaryDTO)
                .collect(Collectors.toList());

        return new PageImpl<>(dtos, pageable, entityPage.getTotalElements());
    }

    /**
     * Retrieves a single ThreatSummaryDTO by threat ID.
     */
    public ThreatSummaryDTO getThreatSummaryById(Long id) {
        return threatAnalysisRepo.findById(id)
                .map(this::toSummaryDTO)
                .orElse(null);
    }

    /**
     * Converts ThreatAnalysisEntity to ThreatSummaryDTO.
     */
    private ThreatSummaryDTO toSummaryDTO(ThreatAnalysisEntity entity) {
        ThreatSummaryDTO dto = new ThreatSummaryDTO();
        dto.setId(entity.getId());
        dto.setTitle("Summary: " + entity.getName());
        dto.setCategory("Cyber Threat");
        dto.setSectors(entity.getIndustries() != null ? String.join(", ", entity.getIndustries()) : "N/A");
        dto.setCountries(entity.getTargetedCountries() != null ? String.join(", ", entity.getTargetedCountries()) : "N/A");
        dto.setRiskLevel(RiskLevel.MEDIUM);  // TODO: Implement actual risk scoring logic
        dto.setAiSummary(entity.getAiSummary());
        dto.setGeneratedAt(entity.getCreated());

        if (entity.getExtraSource() != null && !entity.getExtraSource().isEmpty()) {
            dto.setExtraSource(Collections.singletonList(entity.getExtraSource()));
        } else {
            dto.setExtraSource(Collections.emptyList());
        }
        return dto;
    }

    /**
     * Returns raw stored ThreatAnalysisEntity list.
     */
    public List<ThreatAnalysisEntity> getAllStoredThreatEntities() {
        return threatAnalysisRepo.findAll();
    }

    /**
     * Generates an AI summary string using OpenAIService based on ThreatAnalysis DTO data.
     */
    private String generateAiSummary(ThreatAnalysis dto) {
        try {
            String name = dto.getName();
            String description = dto.getDescription();

            if (name == null || description == null) {
                System.err.println("❌ Name or description is null for ThreatAnalysis ID: " + dto.getId());
                return "AI summary unavailable";
            }

            List<String> indicatorSummaries = Optional.ofNullable(dto.getIndicators())
                    .orElse(Collections.emptyList())
                    .stream()
                    .map(IndicatorDTO::getIndicatorSummary)
                    .filter(summary -> summary != null && !summary.isBlank())
                    .collect(Collectors.toList());

            if (indicatorSummaries.isEmpty()) {
                System.out.println("⚠️ No usable indicator summaries found. Proceeding with limited context.");
            }

            System.out.println("🧠 Generating AI Summary for:");
            System.out.println("🔹 Name: " + name);
            System.out.println("🔹 Description: " + description);
            System.out.println("🔹 Indicators: " + indicatorSummaries);

            String aiSummary = openAIService.generateThreatSummary(name, description, indicatorSummaries);

            if (aiSummary == null || aiSummary.isBlank()) {
                System.err.println("⚠️ AI service returned an empty or null summary for: " + name);
                System.err.println("📄 Full DTO: " + dto);
                return "AI summary unavailable";
            }

            System.out.println("✅ AI summary generated (preview): " + aiSummary.substring(0, Math.min(aiSummary.length(), 100)) + "...");
            return aiSummary;

        } catch (Exception e) {
            System.err.println("❌ Exception during AI summary generation for: " + dto.getName() +
                    " - " + e.getClass().getSimpleName() + ": " + e.getMessage());
            return "AI summary unavailable";
        }
    }

    /**
     * Regenerates AI summaries for stored threats missing them.
     */
    public void regenerateMissingSummaries() {
        List<ThreatAnalysisEntity> allThreats = threatAnalysisRepo.findAll();

        for (ThreatAnalysisEntity entity : allThreats) {
            if (entity.getAiSummary() == null || entity.getAiSummary().isBlank()) {
                System.out.println("🔁 Regenerating missing AI summary for: " + entity.getName());
                ThreatAnalysis dto = toDTO(entity);
                String newSummary = generateAiSummary(dto);
                entity.setAiSummary(newSummary);
                threatAnalysisRepo.save(entity);
            }
        }
    }

    /**
     * Retrieves summaries for threats with missing AI summaries.
     */
    public List<ThreatSummaryDTO> getSummariesWithMissingAiSummary() {
        List<ThreatAnalysisEntity> entitiesWithMissingSummary = threatAnalysisRepo.findByAiSummaryIsNullOrEmpty();

        return entitiesWithMissingSummary.stream()
                .map(this::toSummaryDTO)
                .collect(Collectors.toList());
    }

    /**
     * Builds a prompt string for AI summary generation.
     */
    private String buildPrompt(String name, String description, List<String> indicatorSummaries) {
        StringBuilder promptBuilder = new StringBuilder();

        promptBuilder.append("Generate a concise AI summary for the following threat:\n");
        if (name != null && !name.isBlank()) {
            promptBuilder.append("Name: ").append(name).append("\n");
        }
        if (description != null && !description.isBlank()) {
            promptBuilder.append("Description: ").append(description).append("\n");
        }
        if (!indicatorSummaries.isEmpty()) {
            promptBuilder.append("Indicators:\n");
            for (String indicator : indicatorSummaries) {
                promptBuilder.append("- ").append(indicator).append("\n");
            }
        }

        promptBuilder.append("\nSummary:");

        return promptBuilder.toString();
    }


    public List<ThreatAnalysis> loadAllDemoThreats() {
        List<String> filenames = List.of(
                "demo-data/threats.json",
                "demo-data/threatsone.json",
                "demo-data/threattwo.json"
        );

        List<ThreatAnalysis> allThreats = new ArrayList<>();

        for (String filename : filenames) {
            InputStream inputStream = null;
            try {
                inputStream = getClass().getClassLoader().getResourceAsStream(filename);
                if (inputStream != null) {
                    List<ThreatAnalysis> threats = objectMapper.readerForListOf(ThreatAnalysis.class).readValue(inputStream);
                    // Generate AI summaries for demo threats
                    threats.forEach(threat -> {
                        String aiSummary = generateAiSummary(threat);
                        threat.setAiSummary(aiSummary);
                    });
                    allThreats.addAll(threats);
                } else {
                    System.err.println("⚠️ Could not find file: " + filename);
                }
            } catch (IOException e) {
                System.err.println("❌ Failed to load demo threats from " + filename + ": " + e.getMessage());
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        System.err.println("❌ Failed to close input stream for " + filename + ": " + e.getMessage());
                    }
                }
            }
        }

        return allThreats;
    }


    /**
     * Returns paginated and filtered demo ThreatSummaryDTO list.
     */
    public Page<ThreatSummaryDTO> getDemoThreatSummaries(int page, int size,
                                                         String countryFilter, String malwareFilter, String riskFilter, String clientName) {

        List<ThreatAnalysis> allThreats = loadAllDemoThreats();
        System.out.println("Loaded total threats: " + allThreats.size());

        Stream<ThreatAnalysis> filteredStream = allThreats.stream();

        // Filter by client
        if (clientName != null && !clientName.isBlank()) {
            String filter = clientName.trim().toLowerCase();
            filteredStream = filteredStream.filter(t ->
                    t.getClientName() != null && t.getClientName().toLowerCase().contains(filter)
            );
            System.out.println("Applied client filter: " + clientName);
        }

        // ... other filters same as before ...

        // Filter by country
        if (countryFilter != null && !countryFilter.isBlank()) {
            String filter = countryFilter.trim().toUpperCase();
            filteredStream = filteredStream.filter(t -> {
                List<String> countries = t.getTargetedCountries();
                if (countries == null) return false;
                return countries.stream()
                        .anyMatch(c -> c != null && c.toUpperCase().equals(filter));
            });
            System.out.println("Applied country filter: " + countryFilter);
        }

        // Filter by malware
        if (malwareFilter != null && !malwareFilter.isBlank()) {
            String filter = malwareFilter.trim().toLowerCase();
            filteredStream = filteredStream.filter(t -> {
                List<String> malwareFamilies = t.getMalwareFamilies();
                if (malwareFamilies == null) return false;
                return malwareFamilies.stream()
                        .anyMatch(m -> m != null && m.equalsIgnoreCase(filter));
            });
            System.out.println("Applied malware filter: " + malwareFilter);
        }

        // Filter by risk level
        if (riskFilter != null && !riskFilter.isBlank()) {
            try {
                RiskLevel level = RiskLevel.valueOf(riskFilter.toUpperCase());
                filteredStream = filteredStream.filter(t -> t.getRiskLevel() == level);
                System.out.println("Applied risk filter: " + riskFilter);
            } catch (IllegalArgumentException e) {
                System.err.println("Invalid risk level: " + riskFilter);
            }
        }

        List<ThreatAnalysis> filteredList = filteredStream.collect(Collectors.toList());
        System.out.println("Filtered total threats: " + filteredList.size());

        int start = Math.min(page * size, filteredList.size());
        int end = Math.min(start + size, filteredList.size());

        List<ThreatSummaryDTO> pageContent = filteredList.subList(start, end).stream()
                .map(this::toSummaryDTOFromDTO)
                .collect(Collectors.toList());

        Pageable pageable = PageRequest.of(page, size);
        return new PageImpl<>(pageContent, pageable, filteredList.size());
    }




    /**
     * Converts ThreatAnalysis DTO to ThreatSummaryDTO for demo data.
     */
    private ThreatSummaryDTO toSummaryDTOFromDTO(ThreatAnalysis dto) {
        ThreatSummaryDTO summary = new ThreatSummaryDTO();
        try {
            summary.setId(dto.getId() != null ? Long.parseLong(dto.getId()) : null);
        } catch (NumberFormatException e) {
            summary.setId(null);
        }
        summary.setTitle("Summary: " + dto.getName());
        summary.setCategory("Cyber Threat");
        summary.setSectors(dto.getIndustries() != null ? String.join(", ", dto.getIndustries()) : "N/A");
        summary.setCountries(dto.getTargetedCountries() != null ? String.join(", ", dto.getTargetedCountries()) : "N/A");
        summary.setRiskLevel(dto.getRiskLevel());  // You can enhance this later with actual risk scoring
        summary.setMalwareFamilies(dto.getMalwareFamilies());
        summary.setClientName(dto.getClientName());
        summary.setAiSummary(dto.getAiSummary());
        summary.setGeneratedAt(dto.getCreated());
        summary.setConfidenceScore(dto.getConfidenceScore() != null ? dto.getConfidenceScore() : 75.0);

        if (dto.getExtraSource() != null && !dto.getExtraSource().isEmpty()) {
            summary.setExtraSource(Collections.singletonList(dto.getExtraSource()));
        } else {
            summary.setExtraSource(Collections.emptyList());
        }
        return summary;
    }

   }
