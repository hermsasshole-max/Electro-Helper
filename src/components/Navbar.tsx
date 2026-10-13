import React from 'react';
import { Lightbulb, Zap, ShieldCheck } from 'lucide-react';

interface NavbarProps {
  activeTab: string;
  onSelectTab: (tab: string) => void;
  onOpenFlashlight: () => void;
}

export const Navbar: React.FC<NavbarProps> = ({
  activeTab,
  onSelectTab,
  onOpenFlashlight,
}) => {
  return (
    <header className="sticky top-0 z-40 w-full bg-slate-900/95 backdrop-blur-md border-b border-slate-800 transition-colors">
      <div className="max-w-7xl mx-auto flex items-center justify-between gap-8 px-4 sm:px-6 h-14">
        {/* Zone 1: Brand Wordmark (Single Text Element) */}
        <a
          href="#"
          onClick={(e) => {
            e.preventDefault();
            onSelectTab('cable_sizing');
          }}
          className="text-base sm:text-lg font-bold tracking-tight text-white flex items-center gap-2 whitespace-nowrap shrink-0 hover:text-sky-400 transition-colors"
        >
          <Zap className="w-5 h-5 text-amber-400 fill-amber-400" />
          <span>ElectroAssistant SA</span>
        </a>

        {/* Zone 2: Single-Line Nav Links */}
        <nav className="hidden md:flex items-center gap-6 text-sm font-medium text-slate-300">
          <button
            onClick={() => onSelectTab('cable_sizing')}
            className={`transition-colors whitespace-nowrap shrink-0 ${
              activeTab === 'cable_sizing' ? 'text-sky-400 font-semibold' : 'hover:text-white'
            }`}
          >
            Cable Sizing
          </button>
          <button
            onClick={() => onSelectTab('coc_audit')}
            className={`transition-colors whitespace-nowrap shrink-0 ${
              activeTab === 'coc_audit' ? 'text-sky-400 font-semibold' : 'hover:text-white'
            }`}
          >
            CoC Audit
          </button>
          <button
            onClick={() => onSelectTab('pinouts')}
            className={`transition-colors whitespace-nowrap shrink-0 ${
              activeTab === 'pinouts' ? 'text-sky-400 font-semibold' : 'hover:text-white'
            }`}
          >
            Pinout Library
          </button>
          <button
            onClick={() => onSelectTab('diagnostics')}
            className={`transition-colors whitespace-nowrap shrink-0 ${
              activeTab === 'diagnostics' ? 'text-sky-400 font-semibold' : 'hover:text-white'
            }`}
          >
            Field Diagnostics
          </button>
          <button
            onClick={() => onSelectTab('kotlin_code')}
            className={`transition-colors whitespace-nowrap shrink-0 ${
              activeTab === 'kotlin_code' ? 'text-sky-400 font-semibold' : 'hover:text-white'
            }`}
          >
            Kotlin Code
          </button>
        </nav>

        {/* Zone 3: 1 Primary Action */}
        <div className="flex items-center gap-3 shrink-0">
          <button
            onClick={onOpenFlashlight}
            className="flex items-center gap-2 px-3 py-1.5 text-xs font-semibold text-slate-900 bg-amber-400 hover:bg-amber-300 active:scale-95 rounded-lg transition-all shadow-sm whitespace-nowrap shrink-0"
            title="Inspect dark distribution board"
          >
            <Lightbulb className="w-4 h-4 fill-slate-900" />
            <span className="hidden sm:inline">DB Torch</span>
          </button>
        </div>
      </div>
    </header>
  );
};
