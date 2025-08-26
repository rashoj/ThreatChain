import React from 'react';
import { useNavigate } from 'react-router-dom';
import './LandingBanner.css'; // Optional for styling

const LandingBanner = () => {
  const navigate = useNavigate();

  return (
    <div className="landing-banner d-flex align-items-center justify-content-center flex-column text-center">
      <h1 className="text-white mb-4">🔐 Welcome to ThreatChain Demo</h1>
      <p className="text-white mb-4">
        This MSSP-focused demo shows AI-enhanced cyber threat intelligence with real-time risk heatmaps, blockchain audit trails, and summaries.
      </p>
     <button className='btn btn-primary btn-lg' onClick={() => navigate('/demo/login')}>
        View Demo Dashboard
      </button>
    </div>
  );
};

export default LandingBanner;
