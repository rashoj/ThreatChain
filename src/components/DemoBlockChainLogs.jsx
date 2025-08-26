import React, { useState } from 'react';

const DemoBlockchainLogs = () => {
  const [threatId, setThreatId] = useState('');
  const [logs, setLogs] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const fetchLogs = async () => {
    if (!threatId.trim()) {
      setError('Please enter a valid Threat ID.');
      return;
    }

    setLoading(true);
    setError('');
    setLogs([]);

    try {
      const response = await fetch(`http://localhost:8081/demo/api/blockchain-logs/demo/threat/${threatId}`);
      if (!response.ok) {
        throw new Error(`Failed to fetch logs: ${response.status} ${response.statusText}`);
      }
      const data = await response.json();
      setLogs(data);
    } catch (err) {
      setError('Error fetching logs: ' + err.message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="container mx-auto p-6">
      <h1 className="text-2xl font-bold mb-4">Demo Blockchain Logs</h1>

      <div className="flex items-center space-x-4 mb-6">
        <input
          type="text"
          placeholder="Enter Threat ID"
          className="border rounded px-4 py-2 w-64"
          value={threatId}
          onChange={(e) => setThreatId(e.target.value)}
        />
        <button
          className="bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-700"
          onClick={fetchLogs}
          disabled={loading}
        >
          {loading ? 'Loading...' : 'Fetch Logs'}
        </button>
      </div>

      {error && <p className="text-red-500 mb-4">{error}</p>}

      {logs.length > 0 ? (
        <div className="overflow-x-auto">
          <table className="min-w-full bg-white shadow rounded">
            <thead>
              <tr className="bg-gray-100 text-left">
                <th className="px-4 py-2">ID</th>
                <th className="px-4 py-2">Transaction Hash</th>
                <th className="px-4 py-2">Action</th>
                <th className="px-4 py-2">Timestamp</th>
                <th className="px-4 py-2">Details</th>
                <th className="px-4 py-2">Hash</th>
                <th className="px-4 py-2">Previous Hash</th>
                <th className="px-4 py-2">Data Snapshot</th>
              </tr>
            </thead>
            <tbody>
              {logs.map((log) => (
                <tr key={log.id} className="border-t">
                  <td className="px-4 py-2">{log.id}</td>
                  <td className="px-4 py-2 break-all">{log.transaction_hash}</td>
                  <td className="px-4 py-2">{log.action}</td>
                  <td className="px-4 py-2">{log.timestamp}</td>
                  <td className="px-4 py-2">{log.details || '-'}</td>
                  <td className="px-4 py-2 break-all">{log.hash}</td>
                  <td className="px-4 py-2 break-all">{log.previous_hash || '-'}</td>
                  <td className="px-4 py-2 break-all">{log.data_snapshot || '-'}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      ) : (
        !loading && <p>No logs found.</p>
      )}
    </div>
  );
};

export default DemoBlockchainLogs;
