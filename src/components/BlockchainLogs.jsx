import React, { useEffect, useState } from 'react';
import './Dashboard.css'; // Reuse your dashboard styling

const BlockchainLogs = () => {
  const [logs, setLogs] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    const fetchLogs = async () => {
      try {
        const response = await fetch('http://localhost:8081/api/blockchain-logs/list', {
          headers: {
            'x-api-key': 'e3a490ef03b1bb5a89cb9b57cf5087dc5794b8e9774e1a8bda6540ed59932650',
            'Content-Type': 'application/json',
          }
        });

        if (!response.ok) {
          throw new Error('Failed to fetch blockchain logs');
        }

        const data = await response.json();
        setLogs(data);
      } catch (err) {
        setError(err.message);
      } finally {
        setLoading(false);
      }
    };

    fetchLogs();
  }, []);

  if (loading) return <div className="dashboard-container mt-5">Loading blockchain logs...</div>;
  if (error) return <div className="dashboard-container mt-5 text-danger">Error: {error}</div>;

  return (
    <div className="dashboard-container mt-5">
      <div className="dashboard-header">
        <h2 style={{color :'white'}}>🛡️ Blockchain Activity Logs</h2>
        <p className="dashboard-subtext">
          Below is a list of immutable logs created during threat analysis.
        </p>
      </div>

      {logs.length === 0 ? (
        <p>No logs found.</p>
      ) : (
        <div className="table-responsive">
          <table className="table table-bordered table-hover table-striped">
            <thead className="table-dark">
              <tr>
                <th>ID</th>
                <th>Event</th>
                <th>Block #</th>
                <th>Tx Hash</th>
                <th>Threat Hash</th>
                <th>Transaction Hash</th>
                <th>Timestamp</th>
                <th>View Threat</th>
              </tr>
            </thead>
            <tbody>
              {logs.map(log => (
                <tr key={log.id}>
                  <td>{log.id}</td>
                  <td>{log.event}</td>
                  <td>{log.block_number || 'N/A'}</td>
                  <td>{log.tx_hash || 'N/A'}</td>
                  <td>{log.threat_hash || 'N/A'}</td>
                  <td>{log.transaction_hash || 'N/A'}</td>
                  <td>
                    {Array.isArray(log.timestamp)
                      ? new Date(
                          log.timestamp[0],
                          log.timestamp[1] - 1,
                          log.timestamp[2],
                          log.timestamp[3],
                          log.timestamp[4],
                          log.timestamp[5]
                        ).toLocaleString()
                      : 'N/A'}
                  </td>
                  
               <td>
                {log.threat_id ? (
                    <a 
                    href={`/threat-feed/${log.threat_id}`}
                    className="btn btn-sm btn-outline-primary"
                    >
                        View
                    </a>
                ) : (
                    'N/A'
                )}
               </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
};

export default BlockchainLogs;