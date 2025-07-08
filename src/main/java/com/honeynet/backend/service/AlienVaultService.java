package com.honeynet.backend.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.honeynet.backend.DTO.IndicatorDTO;
import com.honeynet.backend.DTO.ThreatAnalysis;
import com.honeynet.backend.DTO.ThreatSummaryDTO;
import com.honeynet.backend.Entity.*;
import com.honeynet.backend.config.AlienVaultConfig;
import com.honeynet.backend.model.ThreatAnalysisRepo;
import com.honeynet.backend.model.ThreatAnalysisRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AlienVaultService {

    private final AlienVaultConfig config;


    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ThreatAnalysisRepo repository;

    @Autowired
    private BlockchainLogService blockchainLogService;

    @Autowired
    private OpenAIService openAIService;

    @Autowired
    private ThreatAnalysisRepository threatAnalysisRepository;
    @Autowired
    private ThreatAnalysisRepo threatAnalysisRepo;

    @Autowired
    public AlienVaultService(AlienVaultConfig config) {
        this.config = config;
    }
    public List<String> getAllCountries() {
        return threatAnalysisRepo.findDistinctCountries();
    }


    /**
     * Fetches subscribed pulses from AlienVault, parses, enriches with AI summary, saves to DB,
     * and logs blockchain events. Skips duplicates based on name and revision.
     *
     * @return List of parsed ThreatAnalysis DTOs.
     */
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
                Optional<ThreatAnalysisEntity> optionalEntity = repository.findByNameAndRevision(dto.getName(), dto.getRevision());

                if (optionalEntity.isPresent()) {
                    ThreatAnalysisEntity entityObj = optionalEntity.get();
                    System.out.println("⚠️ Updating existing: " + dto.getName() + " | revision " + dto.getRevision());

                    // Preserve existing AI summary if already present
                    if (entityObj.getAiSummary() == null || entityObj.getAiSummary().isEmpty()) {
                        String aiSummary = generateAiSummary(dto);
                        System.out.println("🧠 Generated AI Summary for " + dto.getName() + ": " + aiSummary);
                        entityObj.setAiSummary(aiSummary);
                    }

                    // Update other fields
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

                    // Update collections
                    entityObj.setMalwareFamilies(new HashSet<>(dto.getMalwareFamilies()));
                    entityObj.setIndustries(new HashSet<>(dto.getIndustries()));
                    entityObj.setTargetedCountries(new HashSet<>(dto.getTargetedCountries()));

                    // Update indicators
                    entityObj.getIndicators().clear();
                    List<IndicatorEntity> indicators = Optional.ofNullable(dto.getIndicators())
                            .orElse(Collections.emptyList())
                            .stream()
                            .map(indicatorDTO -> {
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
                                indicatorEntity.setThreatAnalysis(entityObj);
                                return indicatorEntity;
                            }).collect(Collectors.toList());
                    entityObj.getIndicators().addAll(indicators);

                    repository.save(entityObj);

                } else {
                    System.out.println("✅ Inserting new: " + dto.getName() + " | revision " + dto.getRevision());
                    ThreatAnalysisEntity unsavedEntity  = toEntity(dto);



                    // Generate AI summary
                    String aiSummary = generateAiSummary(dto);
                    unsavedEntity.setAiSummary(aiSummary);

                    ThreatAnalysisEntity savedThreat = repository.saveAndFlush(unsavedEntity);

                    // Log blockchain event
                    BlockchainLog log = new BlockchainLog();
                    log.setEvent("Threat logged: " + dto.getName());
                    log.setBlockNumber(String.valueOf(System.currentTimeMillis()));
                    log.setTransactionHash("0x" + UUID.randomUUID().toString().replace("-", "").substring(0, 10));
                    log.setTimestamp(LocalDateTime.now());
                    log.setThreat(savedThreat);
                    blockchainLogService.saveLog(log);
                }
            }
            return pulseList;

        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }



    /**
     * Converts ThreatAnalysis DTO to ThreatAnalysisEntity including IndicatorEntities.
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
                    tagEntity.setThreatAnalysis(entity); // set back reference
                    return tagEntity;
                })
                .collect(Collectors.toList());
        entity.setTags(tagEntities);

        entity.setAttackIds(dto.getAttackIds());
        entity.setReferenceLinks(dto.getReferences());

        entity.setExtraSource(dto.getExtraSource());
        entity.setAiSummary(generateAiSummary(dto));

        entity.setMalwareFamilies(new HashSet<>(dto.getMalwareFamilies()));
        entity.setIndustries(new HashSet<>(dto.getIndustries()));
        entity.setTargetedCountries(new HashSet<>(dto.getTargetedCountries()));


// Save parent, which will cascade to tags if cascade is set properly



        List<IndicatorEntity> indicators = Optional.ofNullable(dto.getIndicators())
                .orElse(Collections.emptyList())
                .stream()
                .map(indicatorDTO -> {
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
                    indicatorEntity.setThreatAnalysis(entity);
                    return indicatorEntity;
                })
                .collect(Collectors.toList());

//        // Clear existing indicators first to avoid orphan deletion errors
//        savedEntity.getIndicators().clear();  // clears old indicators, tells Hibernate to delete orphans
//
//// Add all new indicators to the existing collection
//        savedEntity.getIndicators().addAll(indicators);

        entity.getIndicators().clear();
        entity.getIndicators().addAll(indicators);
        return entity;
    }

    /**
     * Converts ThreatAnalysisEntity to ThreatAnalysis DTO including IndicatorDTOs.
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
        dto.setTargetedCountries(new ArrayList<>(entity.getTargetedCountries()));
        dto.setMalwareFamilies(new ArrayList<>(entity.getMalwareFamilies()));
        dto.setAttackIds(entity.getAttackIds());
        dto.setReferences(entity.getReferenceLinks());
        dto.setIndustries(new ArrayList<>(entity.getIndustries()));
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
                })
                .collect(Collectors.toList());

        dto.setIndicators(indicators);
        return dto;
    }

    /**
     * Retrieve ThreatAnalysis DTO by ID.
     */
    public ThreatAnalysis getThreatById(Long id) {
        return repository.findById(id)
                .map(this::toDTO)
                .orElse(null);
    }

    /**
     * Retrieve all stored ThreatAnalysis DTOs.
     */
    public List<ThreatAnalysis> getAllStoredThreats() {
        return repository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
    /**
     * Retrieve threat summaries with sorting and ordering using enum.
     */
    public List<ThreatSummaryDTO> getPaginatedThreatSummaries(String sortBy, String order) {
        ThreatSortField sortField = ThreatSortField.fromString(sortBy);

        List<ThreatSummaryDTO> summaries = repository.findAll().stream()
                .map(this::toSummaryDTO)
                .collect(Collectors.toList());

        Comparator<ThreatSummaryDTO> comparator = switch (sortField) {
            case TITLE -> Comparator.comparing(ThreatSummaryDTO::getTitle, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER));
            case RISK_SCORE -> Comparator.comparingInt(dto ->
                    dto.getRiskLevel() != null ? dto.getRiskLevel().getLevel() : 0);
            case GENERATED_AT -> Comparator.comparing(ThreatSummaryDTO::getGeneratedAt, Comparator.nullsLast(Comparator.naturalOrder()));
            default -> throw new IllegalArgumentException("Unknown sort field: " + sortField);

        };

        if ("desc".equalsIgnoreCase(order)) {
            comparator = comparator.reversed();
        }

        summaries.sort(comparator);
        return summaries;
    }

    /**
     * Retrieve a single ThreatSummaryDTO by threat ID.
     */
    public ThreatSummaryDTO getThreatSummaryById(Long id) {
        return repository.findById(id)
                .map(this::toSummaryDTO)
                .orElse(null);
    }

    /**
     * Convert ThreatAnalysisEntity to ThreatSummaryDTO.
     */
    private ThreatSummaryDTO toSummaryDTO(ThreatAnalysisEntity entity) {

        ThreatSummaryDTO dto = new ThreatSummaryDTO();
        dto.setId(entity.getId());
        dto.setTitle("Summary: " + entity.getName());
        dto.setCategory("Cyber Threat");
        dto.setSectors(entity.getIndustries() != null ? String.join(", ", entity.getIndustries()) : "N/A");
        dto.setCountries(entity.getTargetedCountries() != null ? String.join(", ", entity.getTargetedCountries()) : "N/A");
        dto.setRiskLevel(RiskLevel.MEDIUM);  // or any RiskLevel value based on your logic
        // Static example, replace with your scoring logic
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
     * Retrieve all stored ThreatAnalysis entities (raw).
     */
    public List<ThreatAnalysisEntity> getAllStoredThreatEntities() {
        return repository.findAll();
    }

    /**
     * Get paginated, sorted threat summaries using enum.
     */
    public Page<ThreatSummaryDTO> getPaginatedThreatSummaries(int page, int size, String sortBy, String sortOrder) {
        ThreatSortField sortField = ThreatSortField.fromString(sortBy);

        Sort.Direction direction = "desc".equalsIgnoreCase(sortOrder) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Sort sort = Sort.by(direction, sortField.getField());
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<ThreatAnalysisEntity> entityPage = repository.findAll(pageable);

        List<ThreatSummaryDTO> dtos = entityPage.getContent().stream()
                .map(this::toSummaryDTO)
                .collect(Collectors.toList());

        return new PageImpl<>(dtos, pageable, entityPage.getTotalElements());
    }
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
                    .filter(summary -> summary != null && !summary.isBlank()) // 💡 Improved filtering
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

            // Final fallback



            System.out.println("✅ AI summary generated (preview): " + aiSummary.substring(0, Math.min(aiSummary.length(), 100)) + "...");
            return aiSummary;

        } catch (Exception e) {
            System.err.println("❌ Exception during AI summary generation for: " + dto.getName() +
                    " - " + e.getClass().getSimpleName() + ": " + e.getMessage());
            return "AI summary unavailable";
        }

    }
    public void regenerateMissingSummaries() {
        List<ThreatAnalysisEntity> allThreats = repository.findAll();

        for (ThreatAnalysisEntity entity : allThreats) {
            if (entity.getAiSummary() == null || entity.getAiSummary().isBlank()) {
                System.out.println("🔁 Regenerating missing AI summary for: " + entity.getName());
                ThreatAnalysis dto = toDTO(entity);
                String newSummary = generateAiSummary(dto);
                entity.setAiSummary(newSummary);
                repository.save(entity);
            }
        }

            }
    public List<ThreatSummaryDTO> getSummariesWithMissingAiSummary() {
        List<ThreatAnalysisEntity> entitiesWithMissingSummary = repository.findByAiSummaryIsNullOrEmpty();

        return entitiesWithMissingSummary.stream()
                .map(this::toSummaryDTO)
                .collect(Collectors.toList());
    }
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

}
