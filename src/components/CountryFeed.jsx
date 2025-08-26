import React, { useState, useEffect } from "react";
import axios from "axios";

function CountryFeed() {
  const [threats, setThreats] = useState([]);
  const [country, setCountry] = useState("");
  const [countries, setCountries] = useState([]);

  // Fetch all distinct countries
  useEffect(() => {
    axios.get("/api/threats/countries")
      .then((res) => setCountries(res.data))
      .catch((err) => console.error("Error fetching countries:", err));
  }, []);

  // Fetch threats by selected country
  useEffect(() => {
    if (country) {
      axios.get(`/api/threats/by-country?country=${country}`)
        .then((res) => setThreats(res.data))
        .catch((err) => console.error("Error fetching threats:", err));
    } else {
      setThreats([]);
    }
  }, [country]);

  return (
    <div className="container">
      <h3 className="mt-4">Threat Feed by Country</h3>

      <div className="form-group my-3">
        <label>Select a Country:</label>
        <select className="form-control" onChange={(e) => setCountry(e.target.value)} value={country}>
          <option value="">-- Select Country --</option>
          {countries.map((c, idx) => (
            <option key={idx} value={c}>{c}</option>
          ))}
        </select>
      </div>

      {country && (
        <div>
          <h5>Showing threats for: <strong>{country}</strong></h5>
          <ul className="list-group mt-3">
            {threats.map((threat, idx) => (
              <li className="list-group-item" key={idx}>
                <strong>{threat.title}</strong><br />
                {threat.summary}<br />
                <small className="text-muted">Country: {threat.country}</small>
              </li>
            ))}
          </ul>
        </div>
      )}
    </div>
  );
}

export default CountryFeed;
