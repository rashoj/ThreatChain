import React from 'react';

const ClientDropdown = ({ clients, selectedClient, onClientChange }) => {
  return (
    <div className="mb-3">
      <label htmlFor="clientSelect" className="form-label">Select Client:</label>
      <select
        id="clientSelect"
        className="form-select"
        value={selectedClient}
        onChange={(e) => onClientChange(e.target.value)}
      >
        {clients.map((client, idx) => (
          <option key={idx} value={client}>
            {client}
          </option>
        ))}
      </select>
    </div>
  );
};

export default ClientDropdown;
