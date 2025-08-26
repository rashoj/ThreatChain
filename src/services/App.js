import ThreatSummaryTable from './components/ThreatSummaryTable';
import AlertsList from './components/AlertsList';
import 'bootstrap/dist/css/bootstrap.min.css';     // ← make sure Bootstrap is loaded
import { loadWorldMap } from './loadMapData';
loadWorldMap();



function App() {
  return (
    <div className="container mt-4">
      <ThreatSummaryTable />
      <hr />
      <AlertsList />
    </div>
  );
}

export default App;
