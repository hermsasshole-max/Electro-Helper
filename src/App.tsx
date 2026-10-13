/**
 * ElectroAssistant SA & HVAC Field Suite
 * SANS 10142-1/2/3 Electrical & HVAC-R Engineering Suite
 */

import React, { useState } from 'react';
import { Navbar } from './components/Navbar';
import { CableSizingScreen } from './components/CableSizingScreen';
import { CoCVerificationScreen } from './components/CoCVerificationScreen';
import { PinoutsScreen } from './components/PinoutsScreen';
import { DiagnosticScreen } from './components/DiagnosticScreen';
import { KotlinCodeViewer } from './components/KotlinCodeViewer';
import { DBFlashlightModal } from './components/DBFlashlightModal';
import {
  Zap,
  ClipboardCheck,
  Cpu,
  Compass,
  FileCode2,
  Lightbulb,
  WifiOff,
} from 'lucide-react';

export default function App() {
  const [activeTab, setActiveTab] = useState<string>('cable_sizing');
  const [isFlashlightOpen, setIsFlashlightOpen] = useState<boolean>(false);

  return (
    <div className="min-h-screen bg-[#0b0f17] text-slate-100 flex flex-col selection:bg-sky-500/30 selection:text-white">
      {/* Top App Bar */}
      <Navbar
        activeTab={activeTab}
        onSelectTab={setActiveTab}
        onOpenFlashlight={() => setIsFlashlightOpen(true)}
      />

      {/* Main Content Viewport */}
      <main className="flex-1 max-w-7xl w-full mx-auto px-4 sm:px-6 pt-5">
        {activeTab === 'cable_sizing' && <CableSizingScreen />}
        {activeTab === 'coc_audit' && <CoCVerificationScreen />}
        {activeTab === 'pinouts' && <PinoutsScreen />}
        {activeTab === 'diagnostics' && <DiagnosticScreen />}
        {activeTab === 'kotlin_code' && <KotlinCodeViewer />}
      </main>

      {/* DB Flashlight Overlay Modal */}
      <DBFlashlightModal
        isOpen={isFlashlightOpen}
        onClose={() => setIsFlashlightOpen(false)}
      />

      {/* Ergonomic Bottom Navigation Bar (Fixed for Mobile & Field Multimeter Use) */}
      <nav className="fixed bottom-0 left-0 right-0 z-40 bg-slate-900/95 backdrop-blur-md border-t border-slate-800 md:hidden">
        <div className="grid grid-cols-5 items-center h-16 px-1 max-w-md mx-auto">
          {/* Tab 1: Cable Sizing */}
          <button
            onClick={() => setActiveTab('cable_sizing')}
            className={`min-h-[48px] flex flex-col items-center justify-center transition-colors ${
              activeTab === 'cable_sizing' ? 'text-sky-400 font-bold' : 'text-slate-400 hover:text-slate-200'
            }`}
          >
            <Zap className="w-5 h-5 mb-1" />
            <span className="text-[10px] tracking-tight leading-none">Cable Sizing</span>
          </button>

          {/* Tab 2: CoC Audit */}
          <button
            onClick={() => setActiveTab('coc_audit')}
            className={`min-h-[48px] flex flex-col items-center justify-center transition-colors ${
              activeTab === 'coc_audit' ? 'text-emerald-400 font-bold' : 'text-slate-400 hover:text-slate-200'
            }`}
          >
            <ClipboardCheck className="w-5 h-5 mb-1" />
            <span className="text-[10px] tracking-tight leading-none">CoC Audit</span>
          </button>

          {/* Tab 3: Pinouts */}
          <button
            onClick={() => setActiveTab('pinouts')}
            className={`min-h-[48px] flex flex-col items-center justify-center transition-colors ${
              activeTab === 'pinouts' ? 'text-amber-400 font-bold' : 'text-slate-400 hover:text-slate-200'
            }`}
          >
            <Cpu className="w-5 h-5 mb-1" />
            <span className="text-[10px] tracking-tight leading-none">Pinouts</span>
          </button>

          {/* Tab 4: Diagnostics */}
          <button
            onClick={() => setActiveTab('diagnostics')}
            className={`min-h-[48px] flex flex-col items-center justify-center transition-colors ${
              activeTab === 'diagnostics' ? 'text-sky-400 font-bold' : 'text-slate-400 hover:text-slate-200'
            }`}
          >
            <Compass className="w-5 h-5 mb-1" />
            <span className="text-[10px] tracking-tight leading-none">Diagnostics</span>
          </button>

          {/* Tab 5: Kotlin Code */}
          <button
            onClick={() => setActiveTab('kotlin_code')}
            className={`min-h-[48px] flex flex-col items-center justify-center transition-colors ${
              activeTab === 'kotlin_code' ? 'text-purple-400 font-bold' : 'text-slate-400 hover:text-slate-200'
            }`}
          >
            <FileCode2 className="w-5 h-5 mb-1" />
            <span className="text-[10px] tracking-tight leading-none">Kotlin .kt</span>
          </button>
        </div>
      </nav>
    </div>
  );
}
