import React, { useEffect, useState } from 'react';

const AlertsList = () => {
  const [alerts, setAlerts] = useState([]);
  const [filteredAlerts, setFilteredAlerts] = useState([]);
  const [clients, setClients] = useState([]);
  const [selectedClient, setSelectedClient] = useState('All');
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const username = 'admin';
  const password = 'secret123';
  const apiKey = 'e3a490ef03b1bb5a89cb9b57cf5087dc5794b8e9774e1a8bda6540ed59932650';
  const basicAuthHeader = 'Basic ' + btoa(username + ':' + password);

  const fetchAlerts = async () => {
    try {
      const response = await fetch('http://localhost:8081/api/alerts', {
        headers: {
          Authorization: basicAuthHeader,
          'X-API-KEY': apiKey,
        },
      });

      if (!response.ok) {
        throw new Error(`Failed to fetch alerts: ${response.status}`);
      }

      const data = await response.json();

      // Mock clientName for demo purpose if not present
      const enrichedData = data.map((alert, idx) => ({
        ...alert,
        clientName: alert.clientName || (idx % 2 === 0 ? 'Acme Corp' : 'CyberShield Inc'),
      }));

      const uniqueClients = ['All', ...new Set(enrichedData.map((a) => a.clientName))];

      setAlerts(enrichedData);
      setFilteredAlerts(enrichedData);
      setClients(uniqueClients);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchAlerts();
  }, []);

  const handleClientChange = (e) => {
    const selected = e.target.value;
    setSelectedClient(selected);
    if (selected === 'All') {
      setFilteredAlerts(alerts);
    } else {
      setFilteredAlerts(alerts.filter((a) => a.clientName === selected));
    }
  };

  const deleteAlert = async (id) => {
    try {
      const response = await fetch(`http://localhost:8081/api/alerts/${id}`, {
        method: 'DELETE',
        headers: {
          Authorization: basicAuthHeader,
          'X-API-KEY': apiKey,
        },
      });

      if (!response.ok) {
        throw new Error('Failed to delete alert');
      }

      const updatedAlerts = alerts.filter((alert) => alert.id !== id);
      setAlerts(updatedAlerts);

      const updatedFiltered = selectedClient === 'All'
        ? updatedAlerts
        : updatedAlerts.filter((a) => a.clientName === selectedClient);

      setFilteredAlerts(updatedFiltered);
    } catch (err) {
      alert(err.message);
    }
  };

  if (loading) return <p>Loading alerts...</p>;
  if (error) return <p className="text-danger">Error: {error}</p>;

  return (
    <div className="container mt-4">
      <h2 className="mb-4">Alerts List</h2>

      <div className="mb-3">
        <label htmlFor="clientFilter" className="form-label">Filter by Client</label>
        <select
          id="clientFilter"
          className="form-select"
          value={selectedClient}
          onChange={handleClientChange}
        >
          {clients.map((client, index) => (
            <option key={index} value={client}>
              {client}
            </option>
          ))}
        </select>
      </div>

      {filteredAlerts.length === 0 ? (
        <p>No alerts found for selected client.</p>
      ) : (
        <ul className="list-group">
          {filteredAlerts.map((alert) => (
            <li
              key={alert.id}
              className="list-group-item d-flex justify-content-between align-items-center"
            >
              <div>
                <strong>{alert.title || 'No Title'}</strong><br />
                <span className="text-muted">{alert.clientName}</span><br />
                {alert.description || 'No Description'}
              </div>
              <button
                onClick={() => deleteAlert(alert.id)}
                className="btn btn-sm btn-danger"
              >
                Delete
              </button>
            </li>
          ))}
        </ul>
      )}
    </div>
  );
};

export default AlertsList;
