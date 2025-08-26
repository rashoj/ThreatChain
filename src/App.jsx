import React, { useState } from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import Login from './components/Login';
import Dashboard from './components/Dashboard';
import ThreatFeed from './components/ThreatFeed';
import HomePage from './components/Homepage';
import 'bootstrap/dist/css/bootstrap.min.css';
import 'bootstrap/dist/js/bootstrap.bundle.min.js';
import SignUp from './components/SignUp';
import BlockchainLogs from './components/BlockchainLogs';
import ThreatDetails from './components/ThreatDetails';
import CountryFeed from './components/CountryFeed';
import ThreatDashboard from './components/ThreatDashboard'; // update path if different\\
import ClientPortal from './components/ClientPortal'; 
import DemoLayout from './components/DemoLayout';
import LandingBanner from './components/LandingBanner';
import DemoDashboard from './components/DemoDashboard';
import DemoLogin from './components/DemoLogin';
import DemoBlockchainLogs from './components/DemoBlockChainLogs';





const App = () => {
const [userEmail, setUserEmail] = useState(() => localStorage.getItem('userEmail'));

  const handleLogout = () => {
    setUserEmail(null);
  };

  const isAuthenticated = !!userEmail;

  return (
    <Router>
      <Routes>
        <Route path='/' element={<HomePage/>}>

        </Route>
        <Route
          path="/login"
          element={
            isAuthenticated ? <Navigate to="/dashboard" /> : <Login onLogin={setUserEmail} />
          }
        />
  <Route
  path="/dashboard/threats"
  element={isAuthenticated ? <ThreatDashboard /> : <Navigate to="/login" />}
/>

        <Route
          path="/dashboard"
          element={
            isAuthenticated ? <Dashboard userEmail={userEmail} onLogout={handleLogout} /> : <Navigate to="/login" />
          }
        />
        <Route
          path="/threat-feed"
          element={
            isAuthenticated ? <ThreatFeed /> : <Navigate to="/login" />
          }
          
        />
                <Route path="/demo-blockchain-logs" element={<DemoBlockchainLogs />} />

          <Route
          path="/demo/dashboard/threats"
          element={
            isAuthenticated
              ? <DemoLayout><ThreatDashboard /></DemoLayout>
              : <Navigate to="/login" />
          }
        />
         {/* Route for demo landing page */}
  <Route path="/demo" element={<LandingBanner />} />
        <Route path="/demo/dashboard/threats" element={<DemoDashboard />} />
  {/* existing routes */}
           {/* ✅ New route for dynamic CountryFeed */}
           <Route
          path="/country-feed"
          element={
            isAuthenticated ? <CountryFeed /> : <Navigate to="/login" />
          }
        />
      

        <Route
  path="demo/dashboard/threats"
  element={isAuthenticated ? <ThreatDashboard /> : <Navigate to="/login" />}
/>

        <Route path="*" element={<Navigate to={isAuthenticated ? "/dashboard" : "/login"} />} />
        <Route path="/signup" element={<SignUp onSignUp={(email) => console.log('Signed up:', email)}/>} />
          <Route path="/blockchain-logs" element={<BlockchainLogs />}/>
          <Route path = "/threat-feed/:id" element={<ThreatDetails />} />
          {/* <Route path="/dashboard" element={<Dashboard />} /> */}

            <Route path="/demo/login" element={<DemoLogin />} />
{/* <Route path="/demo/dashboard/client" element={<ClientDashboard />} /> */}
<Route path="/demo/dashboard/mssp" element={<ThreatDashboard />} />

      </Routes>
    </Router>

   
    
  );
};

export default App;