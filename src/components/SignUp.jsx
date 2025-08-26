import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import logo from '../assets/logo.png';
import './SignUp.css';

const SignUp = ({ onSignUp }) => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [success, setSuccess] = useState(false);
  const navigate = useNavigate();

  const handleSubmit = (e) => {
    e.preventDefault();

    if (!email.includes('@') || password.length < 6) {
      setError('Enter a valid email and password (min 6 characters).');
      return;
    }

    setError('');
    setSuccess(true);
    localStorage.setItem('userEmail', email);

    if (onSignUp) onSignUp(email);

    // Redirect to login after 2 seconds
    setTimeout(() => {
      navigate('/login');
    }, 2000);
  };

  return (
    <div className="signup-wrapper">
      {/* Navbar */}
      <nav className="navbar navbar-dark fixed-top bg-transparent px-4 py-2">
        <a className="navbar-brand d-flex align-items-center" href="/">
          <img src={logo} alt="ThreatChain" className="navbar-logo me-2" />
          <span>ThreatChain</span>
        </a>
      </nav>

      {/* Signup Form */}
      <div className="signup-overlay d-flex justify-content-center align-items-center">
        <div className="signup-card">
          <h2 className="signup-title">🛡️ Create Account</h2>
          <p className="signup-subtitle">Join ThreatChain to monitor real-time threats</p>

          {success && (
            <p className="signup-success text-success text-center">
              ✅ Account created! Redirecting to login...
            </p>
          )}

          <form onSubmit={handleSubmit}>
            <input
              type="email"
              className="signup-input"
              placeholder="Email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              required
            />
            <input
              type="password"
              className="signup-input mt-2"
              placeholder="Password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
            />
            {error && <p className="signup-error">{error}</p>}
            <button type="submit" className="signup-button mt-3">Sign Up</button>
          </form>
        </div>
      </div>
    </div>
  );
};

export default SignUp;
