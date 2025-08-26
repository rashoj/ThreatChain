import React, { useEffect, useState } from 'react';
import { fetchPaginatedThreatSummaries } from '../services/threatSummaryService';

const ThreatSummaryTable = () => {
  const [threats, setThreats] = useState([]);
  const [page, setPage] = useState(0);
  const [size, setSize] = useState(10);
  const [sortBy, setSortBy] = useState('created');
  const [order, setOrder] = useState('desc');
  const [totalPages, setTotalPages] = useState(0);

  useEffect(() => {
    fetchPaginatedThreatSummaries(page, size, sortBy, order).then(data => {
      setThreats(data.content);
      setTotalPages(data.totalPages);
    });
  }, [page, size, sortBy, order]);

  const handleSortChange = (field) => {
    setSortBy(field);
    setOrder(order === 'asc' ? 'desc' : 'asc');
  };

  return (
    <div>
      <h3>Threat Summaries</h3>
      <table className="table table-striped">
        <thead>
          <tr>
            <th onClick={() => handleSortChange('name')}>Title</th>
            <th onClick={() => handleSortChange('created')}>Created</th>
            <th>Category</th>
            <th>Countries</th>
            <th>Sectors</th>
            <th>Risk Score</th>
          </tr>
        </thead>
        <tbody>
          {threats.map((summary) => (
            <tr key={summary.id}>
              <td>{summary.title}</td>
              <td>{new Date(summary.generated_at).toLocaleString()}</td>
              <td>{summary.category}</td>
              <td>{summary.countries}</td>
              <td>{summary.sectors}</td>
              <td>{summary.risk_score}</td>
            </tr>
          ))}
        </tbody>
      </table>

      <div className="d-flex justify-content-between align-items-center">
        <button disabled={page === 0} onClick={() => setPage(page - 1)}>Previous</button>
        <span>Page {page + 1} of {totalPages}</span>
        <button disabled={page + 1 >= totalPages} onClick={() => setPage(page + 1)}>Next</button>
      </div>
    </div>
  );
};

export default ThreatSummaryTable;
