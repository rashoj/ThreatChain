import React from 'react';
import { Link } from 'react-router-dom';
import logo from '../assets/logo.png';
import BlockchainLogs from './BlockchainLogs';

const Navbar = ({ onLogout }) => {
//   return (
//     <nav style={{ padding: '1rem', backgroundColor: '#1e40af', color: 'white' }}>
//       <Link to="/dashboard" style={{ marginRight: '1rem', color: 'white' }}>Dashboard</Link>
//       <Link to="/threat-feed" style={{ marginRight: '1rem', color: 'white' }}>Threat Feed</Link>
//       <button onClick={onLogout} style={{ background: '#dc2626', color: 'white', border: 'none', padding: '0.5rem 1rem', borderRadius: '5px' }}>
//         Logout
//       </button>
//     </nav>
//   );
// };
return (
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
      <Link to="/dashboard" style={{ marginRight: '1rem', color: 'white' , textDecoration:'auto' }}>Dashboard</Link>
    </li>
    <li className="nav-item">
    <Link to="/threat-feed" style={{ marginRight: '1rem', color: 'white', textDecoration: 'auto' }}>Threat Feed</Link>
    </li>
    <li className="nav-item">
      <Link to="/blockchain-logs" style={{ marginRight: '1rem', color: 'white', textDecoration: 'auto' }} >BlockchainLogs</Link>
     </li>
  </ul>
        </nav>

);
}
export default Navbar;