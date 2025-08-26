import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import './DemoLogin.css';
import logo from '../assets/logo.png'; // adjust path based on your structure

const DemoLogin = () => {
  const [selectedRole, setSelectedRole] = useState('');
  const navigate = useNavigate();

  const handleLogin = () => {
    if (selectedRole) {
      localStorage.setItem('userRole', selectedRole);
      navigate('/demo/dashboard/threats');
    } else {
      alert('Please select a role');
    }
  };

  return (
    <div className="demo-login-container">
      <div className="demo-login-box">
        <img src={logo} alt="ThreatChain Logo" className="demo-login-logo" />
        <h2>Login to ThreatChain Demo</h2>
        <p className="demo-subtext">For demo access only. Select your role below.</p>
        <select
          value={selectedRole}
          onChange={(e) => setSelectedRole(e.target.value)}
        >
          <option value="">Select Role</option>
          <option value="mssp">MSSP</option>
          <option value="client">Client</option>
        </select>
        <button onClick={handleLogin}>View Dashboard</button>
      </div>
    </div>
  );
};

export default DemoLogin;
