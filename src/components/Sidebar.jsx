import React from "react";
import { Link } from 'react-router-dom';

const Sidebar = () => {
    return (
        <div className="sidebar">
            <ul>
                <li><Link to="/" className="block py-2">Dashboard</Link></li>
                <li><Link to="/threats" className="block py-2">Threat Feed</Link></li>
                <li><Link to="/logs" className="block py-2">Blockchain Logs</Link></li>
                <li><Link to="/deception" className="block py-2">AI Deception</Link></li>

            </ul>

        </div>
    );
};

export default Sidebar;