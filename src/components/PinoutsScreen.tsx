import React, { useState } from 'react';
import { PINOUT_DATA } from '../data/pinoutsData';
import { PinoutDiagram } from '../types/sans';
import {
  Search,
  Cpu,
  Zap,
  Network,
  Info,
  ChevronRight,
  Sparkles,
  Cable,
} from 'lucide-react';

export const PinoutsScreen: React.FC = () => {
  const [selectedCategory, setSelectedCategory] = useState<'all' | 'hvac' | 'relays_starters' | 'comms_power'>('all');
  const [searchQuery, setSearchQuery] = useState('');
  const [activeDeviceId, setActiveDeviceId] = useState<string>(PINOUT_DATA[0].id);
  const [highlightedPin, setHighlightedPin] = useState<string | null>(null);

  const filteredDevices = PINOUT_DATA.filter((device) => {
    const matchesCategory = selectedCategory === 'all' || device.category === selectedCategory;
    const matchesQuery =
      device.title.toLowerCase().includes(searchQuery.toLowerCase()) ||
      device.subtitle.toLowerCase().includes(searchQuery.toLowerCase()) ||
      (device.manufacturer && device.manufacturer.toLowerCase().includes(searchQuery.toLowerCase())) ||
      device.description.toLowerCase().includes(searchQuery.toLowerCase());
    return matchesCategory && matchesQuery;
  });

  const activeDevice = PINOUT_DATA.find((d) => d.id === activeDeviceId) || filteredDevices[0] || PINOUT_DATA[0];

  return (
    <div className="space-y-6 pb-24">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 border-b border-slate-800 pb-4">
        <div>
          <div className="flex items-center gap-2 text-xs font-medium text-sky-400 mb-1">
            <Cpu className="w-4 h-4" />
            <span>Field Technical Pinout Library & Schematics</span>
          </div>
          <h1 className="text-xl sm:text-2xl font-bold text-white tracking-tight">
            HVAC Controls, Relays & Inverter Pinouts
          </h1>
          <p className="text-xs sm:text-sm text-slate-400 mt-1">
            Interactive vector diagrams, terminal mappings, and installation guidelines.
          </p>
        </div>
      </div>

      {/* Search & Category Filter */}
      <div className="flex flex-col sm:flex-row gap-3">
        <div className="relative flex-1">
          <Search className="w-4 h-4 text-slate-400 absolute left-3.5 top-1/2 -translate-y-1/2" />
          <input
            type="text"
            placeholder="Search Eliwell, Carel, Dixell, 5-2-1, RJ45, Sunsynk, ATS..."
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            className="w-full pl-9 pr-4 py-2.5 bg-slate-900 border border-slate-800 rounded-xl text-xs sm:text-sm text-white placeholder-slate-500 focus:border-sky-500 outline-none"
          />
        </div>

        <div className="flex items-center gap-1.5 p-1 bg-slate-900 rounded-xl border border-slate-800 overflow-x-auto">
          {[
            { id: 'all', label: 'All Interfaces' },
            { id: 'hvac', label: 'HVAC & Cold Room' },
            { id: 'relays_starters', label: 'Relays & Starters' },
            { id: 'comms_power', label: 'Comms & Inverters' },
          ].map((cat) => (
            <button
              key={cat.id}
              onClick={() => setSelectedCategory(cat.id as any)}
              className={`px-3 py-1.5 text-xs font-semibold rounded-lg whitespace-nowrap transition-colors ${
                selectedCategory === cat.id
                  ? 'bg-sky-600 text-white shadow-sm'
                  : 'text-slate-400 hover:text-white'
              }`}
            >
              {cat.label}
            </button>
          ))}
        </div>
      </div>

      {/* Main Layout: Device Picker vs Detail & Schematic View */}
      <div className="grid grid-cols-1 lg:grid-cols-12 gap-6">
        {/* Left: Device Selection List */}
        <div className="lg:col-span-4 space-y-2">
          <label className="text-xs font-semibold text-slate-400 uppercase tracking-wider block">
            Hardware Profiles ({filteredDevices.length})
          </label>
          <div className="space-y-1.5 max-h-[600px] overflow-y-auto pr-1">
            {filteredDevices.map((device) => {
              const isSelected = device.id === activeDevice.id;
              return (
                <button
                  key={device.id}
                  onClick={() => {
                    setActiveDeviceId(device.id);
                    setHighlightedPin(null);
                  }}
                  className={`w-full text-left p-3 rounded-xl border transition-all flex items-center justify-between ${
                    isSelected
                      ? 'bg-sky-950/50 border-sky-500 text-white shadow-sm'
                      : 'bg-slate-900 border-slate-800 text-slate-400 hover:border-slate-700 hover:text-slate-300'
                  }`}
                >
                  <div className="pr-2">
                    <span className="text-xs font-bold text-white block truncate">{device.title}</span>
                    <span className="text-[11px] text-slate-400 block truncate mt-0.5">{device.subtitle}</span>
                    <span className="text-[10px] text-sky-400 font-mono mt-1 block">
                      {device.terminals.length} Terminals · {device.manufacturer}
                    </span>
                  </div>
                  <ChevronRight
                    className={`w-4 h-4 shrink-0 transition-transform ${
                      isSelected ? 'text-sky-400 translate-x-0.5' : 'text-slate-600'
                    }`}
                  />
                </button>
              );
            })}
          </div>
        </div>

        {/* Right: Active Device Schematics & Pinout Table */}
        <div className="lg:col-span-8 space-y-5">
          {/* Header Card */}
          <div className="bg-slate-900 rounded-2xl p-5 border border-slate-800">
            <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-2 border-b border-slate-800 pb-3">
              <div>
                <span className="text-xs font-bold font-mono text-sky-400 uppercase">
                  {activeDevice.manufacturer}
                </span>
                <h2 className="text-lg font-bold text-white tracking-tight mt-0.5">
                  {activeDevice.title}
                </h2>
                <p className="text-xs text-slate-400 mt-0.5">{activeDevice.subtitle}</p>
              </div>
              <span className="text-xs font-mono bg-slate-950 text-slate-300 px-3 py-1 rounded-lg border border-slate-800 self-start sm:self-center">
                {activeDevice.category.toUpperCase()}
              </span>
            </div>

            <p className="text-xs text-slate-300 mt-3 leading-relaxed">
              {activeDevice.description}
            </p>

            {/* Interactive Vector Schematic Area */}
            <div className="mt-4 p-4 rounded-xl bg-slate-950 border border-slate-800 flex flex-col items-center justify-center">
              <span className="text-[11px] font-mono text-slate-400 mb-3 self-start">
                INTERACTIVE SCHEMATIC DIAGRAM
              </span>

              {/* Render dynamic schematic based on device type */}
              {renderVectorSchematic(activeDevice.schematicSvgType, highlightedPin)}
            </div>
          </div>

          {/* Terminal Pinouts Table */}
          <div className="bg-slate-900 rounded-2xl p-5 border border-slate-800 space-y-3">
            <h3 className="text-sm font-bold text-white flex items-center justify-between">
              <span>Terminal Block Wiring Reference</span>
              <span className="text-xs font-normal text-slate-400">
                Hover or tap row to highlight pin
              </span>
            </h3>

            <div className="overflow-x-auto">
              <table className="w-full text-xs text-left">
                <thead>
                  <tr className="border-b border-slate-800 text-slate-400 font-semibold">
                    <th className="py-2.5 px-3">Pin #</th>
                    <th className="py-2.5 px-3">Terminal Label</th>
                    <th className="py-2.5 px-3">Circuit Function</th>
                    <th className="py-2.5 px-3">Wire Color / Level</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-slate-800/60">
                  {activeDevice.terminals.map((term) => {
                    const isHighlighted = highlightedPin === term.pin;
                    return (
                      <tr
                        key={term.pin}
                        onMouseEnter={() => setHighlightedPin(term.pin)}
                        onMouseLeave={() => setHighlightedPin(null)}
                        onClick={() =>
                          setHighlightedPin(highlightedPin === term.pin ? null : term.pin)
                        }
                        className={`transition-colors cursor-pointer ${
                          isHighlighted
                            ? 'bg-sky-950/60 text-white font-medium'
                            : 'hover:bg-slate-800/40 text-slate-300'
                        }`}
                      >
                        <td className="py-2.5 px-3 font-mono font-bold text-sky-400">
                          {term.pin}
                        </td>
                        <td className="py-2.5 px-3 font-semibold text-white">
                          {term.label}
                        </td>
                        <td className="py-2.5 px-3 text-slate-300">{term.function}</td>
                        <td className="py-2.5 px-3">
                          <div className="flex items-center gap-1.5">
                            {term.wireColorHint && (
                              <span
                                className="w-2.5 h-2.5 rounded-full border border-slate-600 inline-block shrink-0"
                                style={{ backgroundColor: getWireColorHex(term.wireColorHint) }}
                              />
                            )}
                            <span className="text-slate-400 font-mono text-[11px]">
                              {term.wireColorHint || term.voltageRating}
                            </span>
                          </div>
                        </td>
                      </tr>
                    );
                  })}
                </tbody>
              </table>
            </div>
          </div>

          {/* SANS & Field Technician Notes */}
          <div className="bg-slate-900 rounded-2xl p-5 border border-slate-800">
            <h3 className="text-xs font-bold text-sky-400 uppercase tracking-wider mb-2 flex items-center gap-1.5">
              <Info className="w-4 h-4" />
              <span>Master Electrician Field Notes & SANS 10142 Considerations</span>
            </h3>
            <ul className="space-y-1.5 text-xs text-slate-300">
              {activeDevice.fieldNotes.map((note, idx) => (
                <li key={idx} className="flex items-start gap-2">
                  <span className="text-sky-400 font-bold shrink-0">•</span>
                  <span className="leading-relaxed">{note}</span>
                </li>
              ))}
            </ul>
          </div>
        </div>
      </div>
    </div>
  );
};

// Helper: Wire Color To CSS Hex
function getWireColorHex(colorName: string): string {
  const lower = colorName.toLowerCase();
  if (lower.includes('blue')) return '#38bdf8';
  if (lower.includes('brown')) return '#92400e';
  if (lower.includes('orange')) return '#f97316';
  if (lower.includes('black')) return '#18181b';
  if (lower.includes('white')) return '#f8fafc';
  if (lower.includes('green')) return '#22c55e';
  if (lower.includes('yellow')) return '#eab308';
  if (lower.includes('red')) return '#ef4444';
  if (lower.includes('purple') || lower.includes('violet')) return '#a855f7';
  if (lower.includes('grey') || lower.includes('gray')) return '#64748b';
  return '#94a3b8';
}

// Render dynamic interactive vector schematic
function renderVectorSchematic(type: string, highlightedPin: string | null) {
  if (type === 'hardstart') {
    return (
      <svg viewBox="0 0 540 240" className="w-full max-w-lg h-auto select-none font-mono">
        <rect x="10" y="10" width="520" height="220" rx="12" fill="#0f172a" stroke="#334155" strokeWidth="2" />
        
        {/* Compressor CSR Circle */}
        <circle cx="430" cy="120" r="45" fill="#1e293b" stroke="#38bdf8" strokeWidth="2" />
        <text x="430" y="90" textAnchor="middle" fill="#38bdf8" fontSize="11" fontWeight="bold">COMPRESSOR</text>
        <circle cx="430" cy="105" r="5" fill={highlightedPin === 'Pin 5' ? '#f59e0b' : '#38bdf8'} />
        <text x="430" y="120" textAnchor="middle" fill="#fff" fontSize="10">C</text>
        <circle cx="410" cy="140" r="5" fill="#38bdf8" />
        <text x="410" y="155" textAnchor="middle" fill="#fff" fontSize="10">R</text>
        <circle cx="450" cy="140" r="5" fill={highlightedPin === 'Pin 2' ? '#f59e0b' : '#38bdf8'} />
        <text x="450" y="155" textAnchor="middle" fill="#fff" fontSize="10">S</text>

        {/* Potential Relay Box */}
        <rect x="150" y="50" width="130" height="130" rx="8" fill="#1e293b" stroke="#f59e0b" strokeWidth="2" />
        <text x="215" y="70" textAnchor="middle" fill="#f59e0b" fontSize="11" fontWeight="bold">POTENTIAL RELAY</text>
        
        {/* Relay Pins */}
        <circle cx="170" cy="100" r="6" fill={highlightedPin === 'Pin 1' ? '#38bdf8' : '#e2e8f0'} />
        <text x="170" y="120" textAnchor="middle" fill="#fff" fontSize="10">PIN 1</text>

        <circle cx="215" cy="150" r="6" fill={highlightedPin === 'Pin 2' ? '#38bdf8' : '#e2e8f0'} />
        <text x="215" y="170" textAnchor="middle" fill="#fff" fontSize="10">PIN 2</text>

        <circle cx="260" cy="100" r="6" fill={highlightedPin === 'Pin 5' ? '#38bdf8' : '#e2e8f0'} />
        <text x="260" y="120" textAnchor="middle" fill="#fff" fontSize="10">PIN 5</text>

        {/* Start Capacitor */}
        <rect x="30" y="80" width="70" height="40" rx="6" fill="#334155" stroke="#94a3b8" />
        <text x="65" y="105" textAnchor="middle" fill="#fff" fontSize="10">START CAP</text>

        {/* Run Capacitor */}
        <rect x="30" y="150" width="70" height="40" rx="6" fill="#1e3a8a" stroke="#60a5fa" />
        <text x="65" y="175" textAnchor="middle" fill="#fff" fontSize="10">RUN CAP</text>

        {/* Interconnecting Wire Lines */}
        <path d="M 100 100 L 164 100" stroke="#f97316" strokeWidth="2" strokeDasharray="3,3" />
        <path d="M 221 150 L 320 150 L 320 180 L 450 180 L 450 145" fill="none" stroke="#eab308" strokeWidth="2" />
        <path d="M 266 100 L 370 100 L 425 105" fill="none" stroke="#ef4444" strokeWidth="2" />
      </svg>
    );
  }

  if (type === 'rj45') {
    const pins = [
      { num: 1, color: '#f97316', stripe: true, label: 'W/ORG' },
      { num: 2, color: '#f97316', stripe: false, label: 'ORG' },
      { num: 3, color: '#22c55e', stripe: true, label: 'W/GRN' },
      { num: 4, color: '#3b82f6', stripe: false, label: 'BLU' },
      { num: 5, color: '#3b82f6', stripe: true, label: 'W/BLU' },
      { num: 6, color: '#22c55e', stripe: false, label: 'GRN' },
      { num: 7, color: '#92400e', stripe: true, label: 'W/BRN' },
      { num: 8, color: '#92400e', stripe: false, label: 'BRN' },
    ];

    return (
      <svg viewBox="0 0 520 180" className="w-full max-w-lg h-auto select-none font-mono">
        <rect x="10" y="10" width="500" height="160" rx="12" fill="#0f172a" stroke="#334155" strokeWidth="2" />
        <text x="260" y="32" textAnchor="middle" fill="#38bdf8" fontSize="12" fontWeight="bold">
          T568B RJ45 MODULAR PLUG PINOUT
        </text>

        {pins.map((p, idx) => {
          const x = 50 + idx * 54;
          return (
            <g key={p.num}>
              {/* Pin contact gold tab */}
              <rect x={x} y="45" width="28" height="20" rx="2" fill="#eab308" stroke="#ca8a04" />
              <text x={x + 14} y="58" textAnchor="middle" fill="#000" fontSize="10" fontWeight="bold">
                {p.num}
              </text>

              {/* Wire Stripe */}
              <rect
                x={x + 4}
                y="70"
                width="20"
                height="60"
                rx="3"
                fill={p.color}
                stroke={p.stripe ? '#ffffff' : '#000000'}
                strokeWidth={p.stripe ? 2 : 1}
                strokeDasharray={p.stripe ? '6,4' : undefined}
              />
              <text x={x + 14} y="145" textAnchor="middle" fill="#cbd5e1" fontSize="9">
                {p.label}
              </text>
            </g>
          );
        })}
      </svg>
    );
  }

  // Generic Controller (Eliwell / Carel / Dixell)
  return (
    <svg viewBox="0 0 540 180" className="w-full max-w-lg h-auto select-none font-mono">
      <rect x="10" y="10" width="520" height="160" rx="10" fill="#0f172a" stroke="#334155" strokeWidth="2" />
      <rect x="25" y="25" width="490" height="40" rx="6" fill="#1e293b" />
      <text x="270" y="50" textAnchor="middle" fill="#38bdf8" fontSize="12" fontWeight="bold">
        SCREW TERMINAL CONNECTOR BLOCK
      </text>

      {/* Terminal pins */}
      {[1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12].map((p, idx) => {
        const x = 35 + idx * 40;
        const isHighlight = highlightedPin === String(p);
        return (
          <g key={p}>
            <circle
              cx={x + 12}
              cy="95"
              r="14"
              fill={isHighlight ? '#38bdf8' : '#1e293b'}
              stroke={isHighlight ? '#ffffff' : '#64748b'}
              strokeWidth="2"
            />
            <text
              x={x + 12}
              y="99"
              textAnchor="middle"
              fill={isHighlight ? '#0f172a' : '#ffffff'}
              fontSize="11"
              fontWeight="bold"
            >
              {p}
            </text>
            <path d={`M ${x + 12} 110 L ${x + 12} 145`} stroke="#475569" strokeWidth="2" />
          </g>
        );
      })}
    </svg>
  );
}
