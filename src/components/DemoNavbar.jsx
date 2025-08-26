// components/DemoNavbar.jsx
import React from 'react';
import './DemoNavbar.css';

const DemoNavbar = ({ viewMode, onToggleView }) => {
  return (
    <nav className="demo-navbar">
      <div className="demo-navbar-brand">⚡ ThreatChain (Demo)</div>
      <ul className="demo-navbar-links">
        <li><a href="/dashboard/threats">Threats</a></li>
        <li><a href="/dashboard/alerts">Alerts</a></li>
        <li><a href="/dashboard/blockchain">Blockchain</a></li>
      </ul>
      <button className="view-toggle-btn" onClick={onToggleView}>
        {viewMode === 'mssp' ? 'Switch to Client View' : 'Switch to MSSP View'}
      </button>
    </nav>
  );
};

export default DemoNavbar;
