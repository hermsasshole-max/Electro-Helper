export type SupplyType = 'single_phase' | 'three_phase';
export type CableConductor = 'copper';
export type InsulationType = 'pvc_70' | 'xlpe_90';

export type InstallationMethod = 
  | 'conduit_insulated' // Method 1 / Ref A: Enclosed in conduit in thermally insulating wall
  | 'conduit_wall'      // Method 3 / Ref B: Enclosed in conduit on a wooden or masonry wall / trunking
  | 'clipped_direct'   // Method 4 / Ref C: Clipped direct to non-metallic surface / cable tray
  | 'underground_duct'; // Method 5 / Ref D: In underground conduit, duct, or direct buried

export interface CableSizeData {
  crossSectionMm2: number;
  // Current carrying capacity in Amperes for different methods (70°C Copper PVC)
  currentCapacityPVC: {
    conduit_insulated: number;
    conduit_wall: number;
    clipped_direct: number;
    underground_duct: number;
  };
  // Current carrying capacity in Amperes (90°C Copper XLPE)
  currentCapacityXLPE: {
    conduit_insulated: number;
    conduit_wall: number;
    clipped_direct: number;
    underground_duct: number;
  };
  // Voltage drop in mV/A/m (Single phase)
  mvPerAmpPerMeter1Ph: number;
  // Voltage drop in mV/A/m (Three phase)
  mvPerAmpPerMeter3Ph: number;
  // Typical recommended max MCB rating (A)
  standardMcbAmps: number;
}

export interface CalculationResult {
  nominalVoltage: number;
  loadCurrentIb: number;
  deratingCt: number;
  deratingCg: number;
  combinedDerating: number;
  requiredCapacityIzPrime: number;
  selectedCableSizeMm2: number;
  cableBaseCapacity: number;
  cableDeratedCapacity: number;
  voltageDropVolts: number;
  voltageDropPercent: number;
  maxAllowedVoltageDropVolts: number;
  maxAllowedVoltageDropPercent: number;
  isVoltageDropCompliant: boolean;
  isCapacityCompliant: boolean;
  recommendedMcbRating: number;
  isOvercurrentCoordinated: boolean;
  sansClauses: string[];
  cableOptions: Array<{
    size: number;
    deratedCapacity: number;
    voltDropVolts: number;
    voltDropPercent: number;
    compliant: boolean;
  }>;
}

export interface CoCTestReading {
  id: string;
  circuitName: string;
  breakerRatingA: number;
  earthContinuityOhms: number; // Max 0.2 Ohms (SANS 10142-1 Clause 8.6.2)
  insulationResistancePNE: number; // Min 1.0 MΩ (Clause 8.6.3)
  insulationResistancePE: number;  // Min 1.0 MΩ
  insulationResistanceNE: number;  // Min 1.0 MΩ
  earthLeakageTripCurrentMa: number; // 30mA rating, typically 15-30mA
  earthLeakageTripTimeMs: number; // Max 30ms (Clause 8.6.4)
  loopImpedanceZsOhms: number; // Zs
  psccKa: number; // Prospective Short Circuit Current
  touchVoltageV: number;
  breakerBreakingCapacityKa: number;
  notes?: string;
}

export interface CoCAuditReport {
  id: string;
  certificateNumber: string;
  contractorName: string;
  accreditationNumber: string;
  clientName: string;
  installationAddress: string;
  date: string;
  supplyType: SupplyType;
  mainBreakerRatingA: number;
  mainConductorSizeMm2: number;
  earthingSystem: 'TN-S' | 'TN-C-S' | 'TT' | 'IT';
  visualChecks: {
    mainIsolatorAccessible: boolean;
    neutralEarthSeparatedCorrectly: boolean;
    dbLegendedAndIdentified: boolean;
    ip2xFingerSafeBarriers: boolean;
    surgeProtectionSpdFunctional: boolean;
    earthElectrodeBonded: boolean;
    geyserAndPipesBonded: boolean;
    cablesSecuredAndGlanded: boolean;
  };
  readings: CoCTestReading[];
  passedInspection: boolean;
}

export interface PinoutDiagram {
  id: string;
  category: 'hvac' | 'relays_starters' | 'comms_power';
  title: string;
  subtitle: string;
  manufacturer?: string;
  description: string;
  terminals: Array<{
    pin: string;
    label: string;
    function: string;
    wireColorHint?: string;
    voltageRating?: string;
  }>;
  schematicSvgType: string;
  fieldNotes: string[];
}

export interface DiagnosticTreeNode {
  id: string;
  treeId: string;
  stepNumber: number;
  title: string;
  instruction: string;
  multimeterSetting?: string;
  safetyWarning?: string;
  testAction?: string;
  expectedReading?: string;
  options: Array<{
    label: string;
    outcomeSummary: string;
    nextNodeId?: string;
    isResolution?: boolean;
    resolutionType?: 'pass' | 'fail' | 'action_required';
    remedySteps?: string[];
    sansRef?: string;
  }>;
}
