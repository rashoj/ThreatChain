// components/DemoLayout.jsx
import React, { useState } from 'react';
import DemoNavbar from './DemoNavbar';

const DemoLayout = ({ children }) => {
  const [viewMode, setViewMode] = useState('mssp');

  const handleToggleView = () => {
    setViewMode(prev => (prev === 'mssp' ? 'client' : 'mssp'));
  };

  return (
    <div>
      {/* ✅ Always show DemoNavbar */}
      <DemoNavbar viewMode={viewMode} onToggleView={handleToggleView} />

      {/* ✅ Safe rendering of children */}
      <div>
        {React.isValidElement(children)
          ? React.cloneElement(children, { viewMode })
          : children}
      </div>
    </div>
  );
};

export default DemoLayout;
