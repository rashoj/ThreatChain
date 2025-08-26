import React from 'react';
import { saveAs } from 'file-saver';
import jsPDF from 'jspdf';
import autoTable from 'jspdf-autotable';

const ReportExport = ({ threats = [] }) => {
  /********************  helpers  ********************/
  /** Pick the first non‑empty date‑like field */
  const pickDate = (t) =>
    t.generated_at ||
    t.generatedAt ||
    t.created ||
    t.createdAt ||
    t.timestamp ||
    t.modified ||
    '';

  /** Convert a date string → local string or "N/A" */
 const formatDate = (dateValue) => {
  if (!dateValue) return 'N/A';

  // Handle array-based date like [2024, 1, 15, 10, 0]
  if (Array.isArray(dateValue)) {
    // Months in JS Date are 0-indexed, so subtract 1 from month
    const [year, month, day, hour = 0, minute = 0, second = 0] = dateValue;
    const date = new Date(year, month - 1, day, hour, minute, second);
    return date.toLocaleString();
  }

  // Fallback to normal string date
  const date = new Date(dateValue);
  return isNaN(date.getTime()) ? 'N/A' : date.toLocaleString();
};


  /** Normalise arrays / undefined */
  const fmt = (v) =>
    Array.isArray(v) ? v.join(', ') : v ? v.toString() : 'N/A';

  /********************  CSV  ********************/
  const exportToCSV = () => {
    if (threats.length === 0) return;

    const header = [
      'ID',
      'Created At',
      'Category',
      'Risk Score',
      'Source IP',
      'Destination IP',
      'Malware Families',
      'Countries',
      'AI Summary',
    ];

    const rows = threats.map((t) => [
      t.id ?? 'N/A',
      formatDate(pickDate(t)),
      t.category || 'N/A',
      t.risk_score || 'N/A',
      t.source_ip || 'N/A',
      t.destination_ip || 'N/A',
      fmt(t.malware_families),
      fmt(t.countries),
      t.ai_summary || 'N/A',
    ]);

    const csv = [header, ...rows]
      .map((row) =>
        row
          .map((cell) => `"${cell.replace(/"/g, '""')}"`)
          .join(',')
      )
      .join('\n');

    saveAs(new Blob([csv], { type: 'text/csv;charset=utf-8;' }), 'threat_report.csv');
  };

  /********************  PDF  ********************/
  const exportToPDF = () => {
    if (threats.length === 0) return;

    const doc = new jsPDF({ orientation: 'p', unit: 'mm', format: 'a4' });
    doc.setFontSize(14).text('Threat Intelligence Report', 14, 15);

    const body = threats.map((t) => [
      t.id ?? 'N/A',
      formatDate(pickDate(t)),
      t.category || 'N/A',
      t.risk_score || 'N/A',
      fmt(t.malware_families),
      fmt(t.countries),
      t.ai_summary || 'N/A',
    ]);

    autoTable(doc, {
      startY: 22,
      head: [
        [
          'ID',
          'Created At',
          'Category',
          'Risk',
          'Malware Families',
          'Countries',
          'AI Summary',
        ],
      ],
      body,
      styles: { fontSize: 8, cellPadding: 2, overflow: 'linebreak' },
      headStyles: { fillColor: [22, 160, 133] },
      columnStyles: {
        6: { cellWidth: 80 }, // wider AI summary column
      },
      theme: 'striped',
      pageBreak: 'auto',
    });

    doc.save('threat_report.pdf');
  };

  /********************  Debug (1× log)  ********************/
  if (threats.length) {
    // log once so you can see what actual date fields look like
    // remove/comment after you’re satisfied
    console.log('⏲️ sample threat dates →', threats.slice(0, 3).map((t) => ({
      id: t.id,
      generated_at: t.generated_at,
      created: t.created,
      generatedAt: t.generatedAt,
      createdAt: t.createdAt,
      timestamp: t.timestamp,
      modified: t.modified,
    })));
  }

  /********************  UI  ********************/
  return (
    <div className="mb-4 d-flex gap-3">
      <button className="btn btn-sm btn-outline-primary" onClick={exportToCSV}>
        Export CSV
      </button>
      <button className="btn btn-sm btn-outline-danger" onClick={exportToPDF}>
        Export PDF
      </button>
    </div>
  );
};

export default ReportExport;
