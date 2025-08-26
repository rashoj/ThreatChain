import React, { useState } from 'react';
import './Login.css';
import logo from '../assets/logo.png';

const Login = ({ onLogin }) => {
  const [email, setEmail] = useState('');
  const [error, setError] = useState('');

  const handleSubmit = (e) => {
    e.preventDefault();
    if (!email.includes('@')) {
      setError('Please enter a valid email address.');
      return;
    }

    setError('');
    localStorage.setItem('userEmail', email);
    onLogin(email);
  };

  return (
    <div className="login-wrapper">
      {/* ThreatChain Navbar */}
      <nav className="navbar navbar-dark fixed-top bg-transparent px-4 py-2">
        <a className="navbar-brand d-flex align-items-center" href="/">
          <img src={logo} alt="ThreatChain" className="navbar-logo me-2" />
          <span>ThreatChain</span>
        </a>
      </nav>

      {/* Centered Login Card */}
      <div className="login-overlay d-flex justify-content-center align-items-center">
        <div className="login-card">
          <h2 className="login-title">🔐 Secure Login</h2>
          <p className="login-subtitle">Access the Threat Intelligence Feed</p>
          <form onSubmit={handleSubmit}>
            <input
              type="email"
              className="login-input"
              placeholder="Enter your email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              autoFocus
            />
            {error && <p className="login-error">{error}</p>}
            <button type="submit" className="login-button">Login</button>
          </form>
        </div>
      </div>
    </div>
  );
};

export default Login;