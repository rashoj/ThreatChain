import React from "react";
import './ThreatFeed.css';

const ThreatCard = ({ threat }) => {
  const {
    title,
    category,
    countries,
    sectors,
    risk_score: riskScore,
    ai_summary: aiSummary,
    generated_at: generatedAt
  } = threat;

  // ✅ Format LocalDateTime array [YYYY, MM, DD, HH, mm, ss]
  const formatDate = (arr) => {
    if (!Array.isArray(arr) || arr.length < 3) return 'N/A';
    const [year, month, day, hour = 0, min = 0, sec = 0] = arr;
    const date = new Date(year, month - 1, day, hour, min, sec);
    return isNaN(date.getTime()) ? 'N/A' : date.toLocaleString();
  };

  const riskClass = !isNaN(riskScore)
    ? riskScore > 75 ? 'danger'
      : riskScore > 50 ? 'warning'
      : 'success'
    : 'secondary';

  return (
    <li className="threat-card-white">
      <div className="threat-section">
        <h3 className="threat-title">{title}</h3>

        <div className="threat-field"><strong>Category:</strong> {category ?? 'N/A'}</div>
        <div className="threat-field"><strong>Sectors:</strong> {sectors ?? 'N/A'}</div>
        <div className="threat-field"><strong>Targeted Countries:</strong> {countries ?? 'N/A'}</div>

        <div className="threat-field">
          <strong>Risk Score:</strong>{' '}
          <span className={`badge bg-${riskClass}`}>{riskScore ?? 'N/A'}</span>
        </div>

        {aiSummary && (
          <div className="ai-summary-box mt-3">
            <strong>🧠 AI Summary:</strong>
            <p className="mt-2">{aiSummary}</p>
          </div>
        )}

        <div className="threat-field text-muted mt-3">
          <small><strong>Generated:</strong> {formatDate(generatedAt)}</small>
        </div>
      </div>
    </li>
  );
};

export default ThreatCard;
