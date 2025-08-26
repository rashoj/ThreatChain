import React from 'react';

function ViewToggle({ viewMode, onChange }) {
  return (
    <div className="btn-group mt-3" role="group" aria-label="View toggle">
      <button
        type="button"
        className={`btn ${viewMode === 'mssp' ? 'btn-primary' : 'btn-outline-primary'}`}
        onClick={() => onChange('mssp')}
      >
        MSSP View
      </button>
      <button
        type="button"
        className={`btn ${viewMode === 'client' ? 'btn-primary' : 'btn-outline-primary'}`}
        onClick={() => onChange('client')}
      >
        Client View
      </button>
    </div>
  );
}

export default ViewToggle;
