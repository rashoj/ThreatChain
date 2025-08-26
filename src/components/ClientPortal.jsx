import React from 'react';

const ClientPortal = ({ onSelectClient, selectedClient }) => {
  const clients = ['ClientA', 'ClientB', 'ClientC']; // Demo clients

  const handleChange = (e) => {
    onSelectClient(e.target.value);
  };

  return (
    <div className="mb-3">
      <label htmlFor="clientSelect" className="form-label">Select Client</label>
      <select
        id="clientSelect"
        className="form-select"
        value={selectedClient}
        onChange={handleChange}
      >
        <option value="">-- All Clients --</option>
        {clients.map((client) => (
          <option key={client} value={client}>{client}</option>
        ))}
      </select>
    </div>
  );
};

export default ClientPortal;
