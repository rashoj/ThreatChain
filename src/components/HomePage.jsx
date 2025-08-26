import React from 'react';
import logo from '../assets/logo.png';
import './HomePage.css'; // Your custom CSS if needed

const HomePage = () => {
  return (
    <>
      {/* Navbar */}
      <nav className="navbar navbar-expand-lg navbar-dark bg-dark px-4">
        <a className="navbar-brand d-flex align-items-center" href="/">
          <img src={logo} alt="ThreatChain" className="navbar-logo d-inline-block align-top me-2" />
          <span className="fw-bold">ThreatChain</span>
        </a>
        <button className="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
          <span className="navbar-toggler-icon"></span>
        </button>
        <ul className="navbar-nav ms-auto d-flex flex-row gap-4">
  <li className="nav-item">
    <a className="nav-link" href="#features">Features</a>
  </li>
  <li className="nav-item">
    <a className="nav-link" href="#pricing">Pricing</a>
  </li>
  <li className="nav-item">
    <a className="nav-link" href="#blog">Blog</a>
  </li>
  <li className="nav-item">
    <a href="/signup" className="btn btn-primary ms-3">Sign Up</a>
  </li>
</ul>
      </nav>

      {/* Hero */}
      <header className="hero-section d-flex flex-column justify-content-center align-items-center text-center">
        <h1 className=" hero-title display-4 fw-bold">Blockchain-Backed Threat Intelligence</h1>
        <p className="lead mb-4">Real-time cybersecurity insights powered by immutable blockchain logging.</p>
        <a href="/dashboard" className="btn btn-outline-light btn-lg">View Dashboard</a>
      </header>

      {/* Features */}
      <section className="container py-5" id="features">
        <h2 className="text-center mb-5">Why ThreatChain?</h2>
        <div className="row text-center">
          <div className="col-md-4 mb-4">
            <div className="feature-card p-4 h-100">
              <div className="display-4 mb-3">⚡</div>
              <h5>Real-Time Threats</h5>
              <p className="text-muted">Get live feeds from AlienVault with fast, actionable intelligence.</p>
            </div>
          </div>
          <div className="col-md-4 mb-4">
            <div className="feature-card p-4 h-100">
              <div className="display-4 mb-3">⛓️</div>
              <h5>Blockchain Logging</h5>
              <p className="text-muted">Ensure audit-ready, tamper-proof logs with decentralized storage.</p>
            </div>
          </div>
          <div className="col-md-4 mb-4">
            <div className="feature-card p-4 h-100">
              <div className="display-4 mb-3">🔔</div>
              <h5>Instant Alerts</h5>
              <p className="text-muted">Stay ahead of breaches with severity-based alerting.</p>
            </div>
          </div>
        </div>
      </section>

      {/* Footer */}
      <footer className="text-center py-4 text-muted border-top">
        © {new Date().getFullYear()} ThreatChain. All rights reserved.
      </footer>
      </>
  );
};

export default HomePage;