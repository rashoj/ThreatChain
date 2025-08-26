import React, { useEffect, useState } from 'react';
import ClientPortal from './ClientPortal';
import ThreatCharts from './Threatchart';
import ThreatTrendsChart from './ThreatTrendsChart';
import ReportExport from './ReportExport';
import AlertsList from './AlertsList';
import ViewToggle from './ViewToggle';
import WorldHeatmap from './WorldHeatmap';
import './DemoThreatDashboard.css';
import { useLocation } from 'react-router-dom';

function ThreatDashboard() {
  const [threats, setThreats] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [selectedThreat, setSelectedThreat] = useState(null);
  const [selectedSummary, setSelectedSummary] = useState(null);
  const [selectedClient, setSelectedClient] = useState('');
  const [selectedCountry, setSelectedCountry] = useState('');
  const [heatmapData, setHeatmapData] = useState([]);
  const [searchTerm, setSearchTerm] = useState('');
  const [viewMode, setViewMode] = useState(() => localStorage.getItem('userRole') || 'mssp');

  useEffect(() => {
    const fetchThreats = async () => {
      try {
        setLoading(true);
        const username = 'admin';
        const password = 'secret123';
        const basicAuth = btoa(`${username}:${password}`);
        let url = 'http://localhost:8081/api/demo/summaries';
        const params = new URLSearchParams();
        if (selectedClient) params.append('clientName', selectedClient);
        if (selectedCountry) params.append('countryFilter', selectedCountry);
        url += `?${params.toString()}`;

        const response = await fetch(url, {
          headers: {
            Authorization: `Basic ${basicAuth}`,
            'x-api-key': 'e3a490ef03b1bb5a89cb9b57cf5087dc5794b8e9774e1a8bda6540ed59932650',
          },
        });

        if (!response.ok) {
          throw new Error(`HTTP ${response.status}: ${response.statusText}`);
        }

        const data = await response.json();
        setThreats(data.content || data);

        // Heatmap Data
        const countryNameMap = {
          US: 'United States',
          UK: 'United Kingdom',
          DE: 'Germany',
          FR: 'France',
          IN: 'India',
          JP: 'Japan',
        };
        const countryCounts = (data.content || data).reduce((acc, threat) => {
          const countries = threat.countries?.split(',') || [];
          countries.forEach((c) => {
            const code = c.trim();
            if (!code) return;
            const fullName = countryNameMap[code];
            if (fullName) {
              acc[fullName] = (acc[fullName] || 0) + 1;
            }
          });
          return acc;
        }, {});
        const heatmapReady = Object.entries(countryCounts).map(([name, value]) => ({
          name,
          value,
        }));
        setHeatmapData(heatmapReady);

        setError(null);
      } catch (err) {
        setError(err.message);
        setThreats([]);
      } finally {
        setLoading(false);
      }
    };

    if (viewMode === 'mssp' || (viewMode === 'client' && selectedClient)) {
      fetchThreats();
    } else {
      setThreats([]);
    }
  }, [selectedClient, selectedCountry, viewMode]);

  const openSummaryModal = (threat) => setSelectedSummary(threat);
  const closeSummaryModal = () => setSelectedSummary(null);
  const openDetailModal = (threat) => setSelectedThreat(threat);
  const closeDetailModal = () => setSelectedThreat(null);

  const modalOverlayStyle = {
    position: 'fixed',
    top: 0,
    left: 0,
    right: 0,
    bottom: 0,
    backgroundColor: 'rgba(0, 0, 0, 0.83)',
    zIndex: 1000,
  };

  const modalContentStyle = {
    maxWidth: '700px',
    margin: 'auto',
    padding: '30px',
    backgroundColor: '#fff',
    borderRadius: '10px',
    position: 'relative',
    animation: 'slideFadeIn 0.4s ease-out',
    color: '#000',
  };

  const filteredThreats = threats.filter((threat) => {
    const search = searchTerm.toLowerCase();
    return (
      threat.title?.toLowerCase().includes(search) ||
      threat.ai_summary?.toLowerCase().includes(search) ||
      threat.countries?.toLowerCase().includes(search)
    );
  });

  const location = useLocation();
  const isDemoPath = location.pathname.startsWith('/demo');

  if (loading) {
    return (
      <div className="mt-5 text-center">
        <div className="spinner-border text-primary" role="status">
          <span className="visually-hidden">Loading...</span>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="alert alert-danger mt-5 text-center">
        Error: {error}{' '}
        <button className="btn btn-link" onClick={() => window.location.reload()}>
          Retry
        </button>
      </div>
    );
  }

  return (
    <div className={isDemoPath ? 'demo-dashboard-wrapper' : 'container'}>
      <div className="row align-items-center mb-4">
        <div className="col-md-6 mb-3 mb-md-0">
          <ViewToggle viewMode={viewMode} onChange={setViewMode} />
        </div>
        {(viewMode === 'client' || viewMode === 'mssp') && (
          <div className="col-md-6 text-md-end">
            <ClientPortal onSelectClient={setSelectedClient} selectedClient={selectedClient} />
            <select
              className="form-select mt-2"
              value={selectedCountry}
              onChange={(e) => setSelectedCountry(e.target.value)}
            >
              <option value="">All Countries</option>
              <option value="US">United States</option>
              <option value="UK">United Kingdom</option>
              <option value="DE">Germany</option>
              <option value="FR">France</option>
              <option value="IN">India</option>
              <option value="JP">Japan</option>
            </select>
          </div>
        )}
      </div>

      {viewMode === 'mssp' && (
        <>
          <div className="mb-3">
            <input
              type="text"
              className="form-control"
              placeholder="🔍 Search threats by title, summary, or country"
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
            />
          </div>

          <p><strong>Selected Client:</strong> {selectedClient || 'All'}</p>
          <p><strong>Threats Count:</strong> {filteredThreats.length}</p>

          <h2 className="mt-4">Imported Threat Summaries</h2>
          <ReportExport threats={filteredThreats} />

          <div className="table-responsive mt-4">
            <table className="table table-bordered table-hover">
              <thead>
                <tr>
                  <th>Threat ID</th>
                  <th>Title</th>
                  <th>Malware</th>
                  <th>Country</th>
                  <th>Risk</th>
                  <th>AI Summary</th>
                </tr>
              </thead>
              <tbody>
                {filteredThreats.map((threat, index) => (
                  <tr key={threat.id || index} style={{ cursor: 'pointer' }} onClick={() => openDetailModal(threat)}>
                    <td>{threat.id}</td>
                    <td>{threat.title}</td>
                    <td>{threat.malware_families}</td>
                    <td>{threat.countries}</td>
                    <td>
                      <span className={`badge ${
                        threat.risk_score?.toLowerCase() === 'high'
                          ? 'bg-danger'
                          : threat.risk_score?.toLowerCase() === 'medium'
                          ? 'bg-warning text-dark'
                          : 'bg-success'
                      }`}>
                        {threat.risk_score}
                      </span>
                    </td>
                    <td>
                      <button
                        className="btn btn-sm btn-outline-primary"
                        onClick={(e) => {
                          e.stopPropagation();
                          openSummaryModal(threat);
                        }}
                      >
                        Show AI Summary
                      </button>
                    </td>
                  </tr>
                ))}
                {filteredThreats.length === 0 && (
                  <tr>
                    <td colSpan="6" className="text-center">No threats matched your search.</td>
                  </tr>
                )}
              </tbody>
            </table>
          </div>

          {filteredThreats.length > 0 && (
            <>
              <div className="heatmap-container">
                <h3 style={{ color: '#fff' }}>🌍 Global Threat Heatmap</h3>
                <div className="heatmap-chart">
                  <WorldHeatmap data={heatmapData} />
                </div>
              </div>

              <div className="mt-5">
                <h4>Threat Trends</h4>
                <ThreatTrendsChart threats={filteredThreats} />
              </div>

              <div className="mt-5">
                <h4>Threat Category Breakdown</h4>
                <ThreatCharts threats={filteredThreats} />
              </div>

              <div className="mt-5">
                <h4>Real-Time Alerts</h4>
                <AlertsList />
              </div>
            </>
          )}
        </>
      )}

      {viewMode === 'client' && !selectedClient && (
        <div className="alert alert-warning">
          Please select a client to view client-specific data.
        </div>
      )}

      {viewMode === 'client' && selectedClient && (
        <>
          <h3>Client View: {selectedClient}</h3>
          <p><strong>Total Threats:</strong> {filteredThreats.length}</p>

          <table className="table table-bordered mt-3">
            <thead>
              <tr>
                <th>Threat ID</th>
                <th>Title</th>
                <th>Risk</th>
              </tr>
            </thead>
            <tbody>
              {filteredThreats.map((threat) => (
                <tr key={threat.id}>
                  <td>{threat.id}</td>
                  <td>{threat.title}</td>
                  <td>
                    <span className={`badge ${
                      threat.risk_score?.toLowerCase() === 'high'
                        ? 'bg-danger'
                        : threat.risk_score?.toLowerCase() === 'medium'
                        ? 'bg-warning text-dark'
                        : 'bg-success'
                    }`}>
                      {threat.risk_score}
                    </span>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </>
      )}

      {/* Modals */}
      {selectedSummary && (
        <div style={modalOverlayStyle} onClick={closeSummaryModal}>
          <div style={modalContentStyle} onClick={(e) => e.stopPropagation()}>
            <h5>AI Summary for Threat #{selectedSummary.id}</h5>
            <p><strong>Summary:</strong> {selectedSummary.ai_summary}</p>
            <p><strong>AI Confidence Score:</strong> {selectedSummary.confidence_score ?? 'N/A'}</p>
            <button className="btn btn-danger mt-3" onClick={closeSummaryModal}>Close</button>
          </div>
        </div>
      )}

      {selectedThreat && (
        <div style={modalOverlayStyle} onClick={closeDetailModal}>
          <div style={modalContentStyle} onClick={(e) => e.stopPropagation()}>
            <h5>Threat Details: {selectedThreat.title}</h5>
            <p><strong>ID:</strong> {selectedThreat.id}</p>
            <p><strong>Malware Families:</strong> {selectedThreat.malware_families}</p>
            <p><strong>Countries:</strong> {selectedThreat.countries}</p>
            <p><strong>Risk Score:</strong> {selectedThreat.risk_score}</p>
            <p><strong>AI Summary:</strong> {selectedThreat.ai_summary}</p>
            <p><strong>Confidence Score:</strong> {selectedThreat.confidence_score ?? 'N/A'}</p>
            <p><strong>Created At:</strong> {selectedThreat.generated_at}</p>
            <p><strong>Source:</strong> {selectedThreat.source}</p>
            <button className="btn btn-dark mt-3" onClick={closeDetailModal}>Close</button>
          </div>
        </div>
      )}
    </div>
  );
}

export default ThreatDashboard;
