import React, { useState, useEffect } from 'react';
import ThreatCard from './ThreatCard';
import './Login.css';
import logo from '../assets/logo.png';
import './ThreatFeed.css';
import { fetchPaginatedThreatSummaries } from '../services/threatSummaryService';
import Login from './Login';

const ThreatFeed = () => {
  const [userEmail, setUserEmail] = useState(() => localStorage.getItem("userEmail"));
  const [threats, setThreats] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [sortBy, setSortBy] = useState('risk_score');
  const [order, setOrder] = useState('desc');

  const pageSize = 5;

  const loadThreats = async () => {
    setLoading(true);
    setError(null);
    try {
      const data = await fetchPaginatedThreatSummaries(page, pageSize, sortBy, order);
      setThreats(data.content);
      setTotalPages(data.totalPages);
    } catch (err) {
      console.error('Error fetching threat summaries:', err);
      setError(err.message || 'Unexpected error');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (userEmail) {
      loadThreats();
    }
  }, [userEmail, page, sortBy, order]);

  const handlePrev = () => {
    if (page > 0) setPage(prev => prev - 1);
  };

  const handleNext = () => {
    if (page < totalPages - 1) setPage(prev => prev + 1);
  };

  const handleSortChange = (e) => {
    const value = e.target.value;
    if (value.includes('_asc')) {
      setSortBy(value.replace('_asc', ''));
      setOrder('asc');
    } else {
      setSortBy(value.replace('_desc', ''));
      setOrder('desc');
    }
    setPage(0); // Reset to first page on sort change
  };

  if (!userEmail) {
    return (
      <Login
        onLogin={(email) => {
          localStorage.setItem('userEmail', email);
          setUserEmail(email);
        }}
      />
    );
  }

  return (
    <div className="threatfeed-wrapper text-white">
      <nav className="navbar navbar-dark fixed-top bg-transparent px-4 py-2">
        <a className="navbar-brand d-flex align-items-center" href="/">
          <img src={logo} alt="ThreatChain" className="navbar-logo me-2" />
          <span>ThreatChain</span>
        </a>
      </nav>

      <div className="container pt-5 mt-5">
        <h2 className="text-center mb-4">Threat Intelligence Feed</h2>

        <div className="text-center mb-3">
          <label className="me-2">Sort by:</label>
          <select value={`${sortBy}_${order}`} onChange={handleSortChange} className="form-select d-inline-block w-auto">
            <option value="risk_score_desc">Risk Score (High to Low)</option>
            <option value="risk_score_asc">Risk Score (Low to High)</option>
            <option value="generated_at_desc">Newest First</option>
            <option value="generated_at_asc">Oldest First</option>
            <option value="title_asc">Title A-Z</option>
            <option value="title_desc">Title Z-A</option>
          </select>
        </div>

        {loading && <p className="text-center">Loading threat summaries...</p>}
        {error && <p className="text-center text-danger">Error: {error}</p>}
        {!loading && !error && (
          <>
            {threats.length === 0 ? (
              <p className="text-center">No summaries found.</p>
            ) : (
              <ul className="list-unstyled">
                {threats.map((threat, index) => (
                  <ThreatCard key={index} threat={threat} />
                ))}
              </ul>
            )}

            <div className="text-center mt-3">
              <button className="btn btn-outline-light me-2" onClick={handlePrev} disabled={page === 0}>
                Previous
              </button>
              <span>Page {page + 1} of {totalPages}</span>
              <button className="btn btn-outline-light ms-2" onClick={handleNext} disabled={page >= totalPages - 1}>
                Next
              </button>
            </div>
          </>
        )}
      </div>
    </div>
  );
};

export default ThreatFeed;
