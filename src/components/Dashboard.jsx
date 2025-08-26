import React, { useEffect, useState } from 'react';
import './Dashboard.css';
import Navbar from './Navbar';
import { fetchPaginatedThreatSummaries } from '../services/threatSummaryService';

const Dashboard = ({ userEmail, onLogout }) => {
  const [summary, setSummary] = useState({
    totalThreats: 0,
    highSeverity: 0,
    topTag: 'N/A',
    topCountry: 'N/A'
  });

  const [recentThreats, setRecentThreats] = useState([]);

  useEffect(() => {
    const fetchThreatData = async () => {
      try {
        const page = 0;
        const size = 100; // Fetch a good amount to compute summary
        const sortBy = 'generated_at';
        const order = 'desc';

        const data = await fetchPaginatedThreatSummaries(page, size, sortBy, order);
        const threats = data.content;

        const totalThreats = data.totalElements || 0;
        const highSeverity = threats.filter(t => t.risk_score >= 75).length;

        const sectorCounts = {};
        const countryCounts = {};

        threats.forEach(t => {
          const sectors = t.sectors?.split(',').map(s => s.trim()) || [];
          const countries = t.countries?.split(',').map(c => c.trim()) || [];

          sectors.forEach(sector => {
            sectorCounts[sector] = (sectorCounts[sector] || 0) + 1;
          });

          countries.forEach(country => {
            countryCounts[country] = (countryCounts[country] || 0) + 1;
          });
        });

        const topTag = Object.entries(sectorCounts).sort((a, b) => b[1] - a[1])[0]?.[0] || 'N/A';
        const topCountry = Object.entries(countryCounts).sort((a, b) => b[1] - a[1])[0]?.[0] || 'N/A';

        setSummary({
          totalThreats,
          highSeverity,
          topTag,
          topCountry
        });

        setRecentThreats(threats.slice(0, 3));
      } catch (err) {
        console.error("Failed to fetch summaries:", err);
      }
    };

    fetchThreatData();
  }, []);

  return (
    <>
      <Navbar onLogout={onLogout} />
      <div className="dashboard-container">
        <div className="dashboard-header">
          <h1>Welcome, {userEmail} 👋</h1>
          <p className="dashboard-subtext">Here’s a quick overview of threat intelligence.</p>
        </div>

        <div className="dashboard-cards">
          <div className="dashboard-card"><p className="card-label">🔥 Total Threats</p><p className="card-value">{summary.totalThreats}</p></div>
          <div className="dashboard-card"><p className="card-label">⚠️ High Severity</p><p className="card-value">{summary.highSeverity}</p></div>
          <div className="dashboard-card"><p className="card-label">🏷️ Top Sector</p><p className="card-value">{summary.topTag}</p></div>
          <div className="dashboard-card"><p className="card-label">🌍 Top Country</p><p className="card-value">{summary.topCountry}</p></div>
        </div>

        <div className="dashboard-section">
          <h2>Recent Summaries</h2>
          <ul className="recent-threats">
            {recentThreats.map((threat, idx) => (
              <li key={idx}>
                <strong>{threat.title || 'Untitled Summary'}</strong> — <span>{threat.sectors || 'N/A'}</span>
              </li>
            ))}
          </ul>
          <a href="/threat-feed" className="view-all-link">View Full Feed →</a>
        </div>

        <div className="dashboard-actions">
          <button className="action-button">🔎 Search</button>
          <button className="action-button">📩 Alerts</button>
          <button className="action-button logout" onClick={onLogout}>🚪 Logout</button>
        </div>
      </div>
    </>
  );
};

export default Dashboard;
