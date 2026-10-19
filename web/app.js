// NEXCORE — C&D Waste Management Digital Marketplace (Web & Mobile PWA)
const STORAGE_KEY = 'nexcore_marketplace_state_v1';

const MATERIAL_CATEGORIES = [
  'Recycled Aggregates',
  'Crushed Concrete',
  'Recycled Bricks',
  'Road-Base Materials',
  'Recovered Metal and Scrap',
  'Mobile Crushing Services'
];

const WASTE_CATEGORIES = [
  'Concrete Debris',
  'Brick and Masonry',
  'Mixed Mineral Debris',
  'Asphalt / Road Planings',
  'Recovered Metal and Scrap',
  'Excavated Rock & Sub-Base'
];

const IMAGE_OPTIONS = [
  { key: 'recycled_concrete', label: 'Recycled Aggregates' },
  { key: 'crushed_concrete', label: 'Crushed Concrete' },
  { key: 'crushed_brick', label: 'Recycled Bricks' },
  { key: 'road_base', label: 'Road-Base Materials' },
  { key: 'scrap_metal', label: 'Recovered Metal & Scrap' }
];

const INITIAL_LISTINGS = [
  {
    id: 'sample_rca_20mm',
    sellerName: 'Metro Aggregate Recovery Ltd (Sample)',
    sellerRole: 'Recycling Service Provider',
    sellerVerified: true,
    listingType: 'MATERIAL',
    title: '20mm Graded Recycled Concrete Aggregate (RCA)',
    category: 'Recycled Aggregates',
    description: 'Clean, screened 20mm recycled concrete aggregate suitable for sub-base, drainage backfill, and temporary haul roads. Processed from segregated commercial structural concrete.',
    conditionSpecs: 'Sieve graded 0-20mm; <1% deleterious material; LA abrasion tested; moisture 4.2%.',
    quantityTonnes: 450,
    pricePerTonne: 16.50,
    unit: 'per tonne',
    priceType: 'Fixed Price',
    location: 'North Industrial Yard, Sector 4',
    preferredCollectionDate: 'Immediate / Mon-Sat',
    deliveryOptions: 'Pickup or Delivery',
    transportIncluded: false,
    contactPreference: 'Platform Quote',
    status: 'Active',
    isSample: true,
    imageKey: 'recycled_concrete',
    createdAt: Date.now() - 86400000 * 5
  },
  {
    id: 'sample_crushed_concrete_40mm',
    sellerName: 'Apex Civil Demolition (Sample)',
    sellerRole: 'Demolition Contractor / Waste Seller',
    sellerVerified: false,
    listingType: 'WASTE',
    title: 'Segregated Structural Concrete Slab Debris (<450mm)',
    category: 'Crushed Concrete',
    description: 'Segregated reinforced concrete foundation debris from commercial warehouse demolition. Rebar sheared and primary broken, ready for mobile jaw crushing or off-site recycling.',
    conditionSpecs: 'Uncontaminated C&D concrete; zero asbestos/gypsum; pieces <450mm.',
    quantityTonnes: 680,
    pricePerTonne: 8.00,
    unit: 'per tonne',
    priceType: 'Negotiable',
    location: 'East Docklands Site, Gate B',
    preferredCollectionDate: 'Within 10 Days',
    deliveryOptions: 'Pickup Only',
    transportIncluded: false,
    contactPreference: 'Platform Quote',
    status: 'Active',
    isSample: true,
    imageKey: 'crushed_concrete',
    createdAt: Date.now() - 86400000 * 4
  },
  {
    id: 'sample_crushed_brick_subbase',
    sellerName: 'GreenCycle Masonry Materials (Sample)',
    sellerRole: 'Recycling Service Provider',
    sellerVerified: true,
    listingType: 'MATERIAL',
    title: 'Crushed Red Brick & Terracotta Sub-Base (40mm Down)',
    category: 'Recycled Bricks',
    description: 'Crushed clay brick and masonry aggregate ideal for lightly trafficked sub-bases, hardstands, compound areas, and landscape drainage layers.',
    conditionSpecs: '40mm down well-graded crushed masonry; magnetically separated.',
    quantityTonnes: 220,
    pricePerTonne: 14.00,
    unit: 'per tonne',
    priceType: 'Fixed Price',
    location: 'West Ring Road Recycling Hub',
    preferredCollectionDate: 'Immediate',
    deliveryOptions: 'Pickup or Delivery',
    transportIncluded: true,
    contactPreference: 'Platform Quote',
    status: 'Active',
    isSample: true,
    imageKey: 'crushed_brick',
    createdAt: Date.now() - 86400000 * 3
  },
  {
    id: 'sample_road_base_type1',
    sellerName: 'TerraCore Circular Infrastructure (Sample)',
    sellerRole: 'Recycling Service Provider',
    sellerVerified: true,
    listingType: 'MATERIAL',
    title: 'Compaction-Grade Recycled Road-Base (Type 1 Unbound)',
    category: 'Road-Base Materials',
    description: 'Blended crushed concrete and mineral aggregate engineered for road sub-bases, parking pads, and heavy equipment access tracks.',
    conditionSpecs: '0-40mm unbound mixture; CBR >80% laboratory test report available on request.',
    quantityTonnes: 850,
    pricePerTonne: 19.00,
    unit: 'per tonne',
    priceType: 'Negotiable',
    location: 'Central Logistics Corridor, Mile 12',
    preferredCollectionDate: 'Scheduled Daily Dispatch',
    deliveryOptions: 'Delivery Available',
    transportIncluded: false,
    contactPreference: 'Platform Quote',
    status: 'Active',
    isSample: true,
    imageKey: 'road_base',
    createdAt: Date.now() - 86400000 * 2
  },
  {
    id: 'sample_recovered_rebar_steel',
    sellerName: 'Vanguard Demolition & Salvage (Sample)',
    sellerRole: 'Demolition Contractor / Waste Seller',
    sellerVerified: true,
    listingType: 'MATERIAL',
    title: 'Recovered Structural Steel Beams & Sheared Rebar Scrap',
    category: 'Recovered Metal and Scrap',
    description: 'Magnetically separated reinforcing steel and structural I-beam offcuts recovered during bridge and commercial frame dismantling.',
    conditionSpecs: 'Clean ferrous steel scrap; Concrete spalling removed; bundled for crane/grab loading.',
    quantityTonnes: 65,
    pricePerTonne: 210.00,
    unit: 'per tonne',
    priceType: 'Negotiable',
    location: 'Harbor Industrial Salvage Yard',
    preferredCollectionDate: 'By Appointment',
    deliveryOptions: 'Pickup or Delivery',
    transportIncluded: false,
    contactPreference: 'Platform Quote',
    status: 'Active',
    isSample: true,
    imageKey: 'scrap_metal',
    createdAt: Date.now() - 86400000
  }
];

function categoryImageUrl(imageKey, category) {
  const key = (imageKey || '').toLowerCase();
  const cat = (category || '').toLowerCase();
  if (key === 'recycled_concrete' || cat.includes('recycled aggregate')) {
    return 'images/img_recycled_aggregates.jpg';
  }
  if (key === 'crushed_concrete' || cat.includes('concrete')) {
    return 'images/img_crushed_concrete.jpg';
  }
  if (key === 'crushed_brick' || cat.includes('brick') || cat.includes('masonry')) {
    return 'images/img_recycled_bricks.jpg';
  }
  if (key === 'road_base' || cat.includes('road') || cat.includes('asphalt') || cat.includes('sub-base')) {
    return 'images/img_road_base.jpg';
  }
  if (key === 'scrap_metal' || cat.includes('metal') || cat.includes('scrap')) {
    return 'images/img_recovered_metal.jpg';
  }
  if (key === 'mobile_crusher' || cat.includes('crusher') || cat.includes('crushing')) {
    return 'images/img_mobile_crusher.jpg';
  }
  return 'images/img_recycled_aggregates.jpg';
}

function imageKeyForCategory(cat) {
  const c = (cat || '').toLowerCase();
  if (c.includes('recycled aggregate')) return 'recycled_concrete';
  if (c.includes('concrete')) return 'crushed_concrete';
  if (c.includes('brick') || c.includes('masonry')) return 'crushed_brick';
  if (c.includes('road') || c.includes('asphalt') || c.includes('sub-base')) return 'road_base';
  if (c.includes('metal') || c.includes('scrap')) return 'scrap_metal';
  return 'recycled_concrete';
}

function statusBadgeClass(status) {
  const s = status || '';
  if (['Active', 'Accepted', 'Completed', 'Delivered', 'Ready for Collection', 'Resolved'].includes(s)) {
    return 'status-badge status-success';
  }
  if (['Quote Received', 'Quotation Sent', 'Scheduled', 'In Progress', 'Dispatched', 'Processing', 'On-Site Service'].includes(s)) {
    return 'status-badge status-purple';
  }
  if (['Cancelled', 'Removed'].includes(s)) {
    return 'status-badge status-danger';
  }
  return 'status-badge status-amber';
}

function loadAppState() {
  try {
    const raw = localStorage.getItem(STORAGE_KEY);
    if (raw) {
      const parsed = JSON.parse(raw);
      return {
        currentTab: parsed.currentTab || 'HOME',
        selectedListingId: null,
        bannerMessage: null,
        userProfile: parsed.userProfile || {
          fullName: 'Site Operations Manager',
          companyName: 'Circular Civil Contractors Ltd',
          email: 'operations@nexcore-circular.com',
          phone: '+1 555-0192',
          city: 'North Industrial Corridor',
          accountType: 'Buyer / Customer',
          isVerified: true,
          savedListingIds: ['sample_rca_20mm']
        },
        customListings: parsed.customListings || [],
        quotations: parsed.quotations || [
          {
            id: 'q_demo_1',
            orderRef: 'RFQ-84920',
            listingId: 'sample_rca_20mm',
            listingTitle: '20mm Graded Recycled Concrete Aggregate (RCA)',
            category: 'Recycled Aggregates',
            sellerName: 'Metro Aggregate Recovery Ltd (Sample)',
            quantityTonnes: 60,
            unitPrice: 16.50,
            totalAmount: 1317.60,
            deliveryPreference: 'Site Delivery Required',
            orderStatus: 'Quote Received',
            paymentStatus: 'Awaiting Confirmation',
            createdAt: 'Today'
          }
        ],
        crusherBookings: parsed.crusherBookings || [
          {
            id: 'cb_demo_1',
            bookingRef: 'CRB-40192',
            customerName: 'Site Operations Manager',
            contactNumber: '+1 555-0192',
            siteLocation: 'East Docklands Demolition Plot 7',
            wasteType: 'Concrete Debris',
            estimatedTonnes: 350,
            serviceType: 'Crushing & Multi-Deck Screening',
            preferredDate: 'Next Monday',
            quotedAmount: 3912.50,
            status: 'Scheduled'
          }
        ],
        reports: parsed.reports || []
      };
    }
  } catch (e) {
    console.warn('State load fallback:', e);
  }
  return {
    currentTab: 'HOME',
    selectedListingId: null,
    bannerMessage: null,
    userProfile: {
      fullName: 'Site Operations Manager',
      companyName: 'Circular Civil Contractors Ltd',
      email: 'operations@nexcore-circular.com',
      phone: '+1 555-0192',
      city: 'North Industrial Corridor',
      accountType: 'Buyer / Customer',
      isVerified: true,
      savedListingIds: ['sample_rca_20mm']
    },
    customListings: [],
    quotations: [
      {
        id: 'q_demo_1',
        orderRef: 'RFQ-84920',
        listingId: 'sample_rca_20mm',
        listingTitle: '20mm Graded Recycled Concrete Aggregate (RCA)',
        category: 'Recycled Aggregates',
        sellerName: 'Metro Aggregate Recovery Ltd (Sample)',
        quantityTonnes: 60,
        unitPrice: 16.50,
        totalAmount: 1317.60,
        deliveryPreference: 'Site Delivery Required',
        orderStatus: 'Quote Received',
        paymentStatus: 'Awaiting Confirmation',
        createdAt: 'Today'
      }
    ],
    crusherBookings: [
      {
        id: 'cb_demo_1',
        bookingRef: 'CRB-40192',
        customerName: 'Site Operations Manager',
        contactNumber: '+1 555-0192',
        siteLocation: 'East Docklands Demolition Plot 7',
        wasteType: 'Concrete Debris',
        estimatedTonnes: 350,
        serviceType: 'Crushing & Multi-Deck Screening',
        preferredDate: 'Next Monday',
        quotedAmount: 3912.50,
        status: 'Scheduled'
      }
    ],
    reports: []
  };
}

const state = loadAppState();

const filters = {
  searchQuery: '',
  category: 'All',
  listingType: 'ALL',
  maxPrice: 300,
  minQty: 0,
  location: '',
  sortBy: 'Recently Listed'
};

const sellDraft = {
  modeIndex: 0, // 0 = WASTE, 1 = MATERIAL
  title: '',
  category: 'Concrete Debris',
  imageKey: 'recycled_concrete',
  quantity: '150',
  price: '14.50',
  priceType: 'Negotiable',
  location: '',
  preferredDate: 'Immediate / Within 7 Days',
  conditionSpecs: 'Segregated C&D material; zero asbestos or hazardous contamination.',
  description: '',
  deliveryOptions: 'Pickup or Delivery',
  transportIncluded: false
};

const crusherDraft = {
  customerName: state.userProfile.fullName,
  contactNumber: state.userProfile.phone,
  siteLocation: state.userProfile.city,
  wasteType: 'Concrete Debris',
  serviceType: 'Crushing & Multi-Deck Screening',
  estimatedTonnes: '350',
  preferredDate: 'Next Monday / Flexible',
  siteAccessInfo: 'Wide gate (>4.5m), compacted hardstand pad available for tracked unit',
  additionalNotes: 'Target output: 20mm RCA and 40mm Type 1 sub-base for on-site reuse + NEXCORE resale.'
};

function saveState() {
  try {
    localStorage.setItem(STORAGE_KEY, JSON.stringify({
      currentTab: state.currentTab,
      userProfile: state.userProfile,
      customListings: state.customListings,
      quotations: state.quotations,
      crusherBookings: state.crusherBookings,
      reports: state.reports
    }));
  } catch (e) {
    console.warn('Could not persist state:', e);
  }
}

function getAllListings() {
  return [...state.customListings, ...INITIAL_LISTINGS];
}

function getFilteredListings() {
  const q = filters.searchQuery.trim().toLowerCase();
  const loc = filters.location.trim().toLowerCase();
  let list = getAllListings().filter(item => {
    if (filters.listingType !== 'ALL' && item.listingType !== filters.listingType) return false;
    if (filters.category !== 'All' && item.category.toLowerCase() !== filters.category.toLowerCase()) return false;
    if (item.pricePerTonne > filters.maxPrice) return false;
    if (item.quantityTonnes < filters.minQty) return false;
    if (loc && !item.location.toLowerCase().includes(loc)) return false;
    if (q) {
      const hay = `${item.title} ${item.category} ${item.description} ${item.sellerName} ${item.location}`.toLowerCase();
      if (!hay.includes(q)) return false;
    }
    return true;
  });

  if (filters.sortBy === 'Lowest Price') {
    list.sort((a, b) => a.pricePerTonne - b.pricePerTonne);
  } else if (filters.sortBy === 'Highest Price') {
    list.sort((a, b) => b.pricePerTonne - a.pricePerTonne);
  } else if (filters.sortBy === 'Highest Quantity') {
    list.sort((a, b) => b.quantityTonnes - a.quantityTonnes);
  } else {
    list.sort((a, b) => (b.createdAt || 0) - (a.createdAt || 0));
  }
  return list;
}

function showBanner(msg) {
  state.bannerMessage = msg;
  renderApp();
}

window.dismissBanner = function() {
  state.bannerMessage = null;
  renderApp();
};

window.navigateTo = function(tab, categoryFilter, typeFilter) {
  state.currentTab = tab;
  state.selectedListingId = null;
  if (categoryFilter) filters.category = categoryFilter;
  if (typeFilter) filters.listingType = typeFilter;
  saveState();
  renderApp();
  window.scrollTo({ top: 0, behavior: 'smooth' });
};

window.openListingDetail = function(listingId) {
  state.currentTab = 'BUY_MATERIALS';
  state.selectedListingId = listingId;
  renderApp();
  window.scrollTo({ top: 0, behavior: 'smooth' });
};

window.closeListingDetail = function() {
  state.selectedListingId = null;
  renderApp();
};

window.toggleSaveListing = function(event, listingId) {
  if (event) event.stopPropagation();
  const saved = state.userProfile.savedListingIds || [];
  const idx = saved.indexOf(listingId);
  if (idx >= 0) {
    saved.splice(idx, 1);
    showBanner('Removed listing from your saved bookmarks.');
  } else {
    saved.push(listingId);
    showBanner('Saved listing to your account bookmarks.');
  }
  state.userProfile.savedListingIds = saved;
  saveState();
  renderApp();
};

function renderMaterialBanner(item) {
  const imgUrl = categoryImageUrl(item.imageKey, item.category);
  return `
    <div class="material-banner">
      <img src="${imgUrl}" alt="${escapeHtml(item.category)}" loading="lazy" />
      <div class="material-banner-scrim">
        <div style="display:flex; gap:6px; align-items:center; flex-wrap:wrap;">
          <span class="banner-pill-dark">${escapeHtml(item.category)}</span>
          ${item.isSample ? '<span class="banner-pill-sample">Sample Listing</span>' : ''}
        </div>
        <div class="banner-spec-footer">
          <span>✦ NEXCORE Verified Spec Format</span>
        </div>
      </div>
    </div>
  `;
}

function renderListingCard(item) {
  const isSaved = (state.userProfile.savedListingIds || []).includes(item.id);
  const typeBadge = item.listingType === 'WASTE' ? 'Debris / Waste' : 'Recycled Material';
  return `
    <div class="glass-card clickable" onclick="openListingDetail('${item.id}')">
      ${renderMaterialBanner(item)}
      <div class="flex-between" style="align-items:flex-start; margin-bottom:8px;">
        <div style="display:flex; gap:6px; flex-wrap:wrap;">
          <span class="${statusBadgeClass(typeBadge)}">${typeBadge}</span>
          <span class="${statusBadgeClass(item.priceType)}">${escapeHtml(item.priceType)}</span>
        </div>
        <button class="glass-chip ${isSaved ? 'active' : ''}" onclick="toggleSaveListing(event, '${item.id}')" title="Save Listing">
          ${isSaved ? '★ Saved' : '☆ Save'}
        </button>
      </div>
      <h3 style="font-size:1.05rem; font-weight:800; margin-bottom:6px; line-height:1.3;">${escapeHtml(item.title)}</h3>
      <p style="font-size:0.84rem; color:var(--text-secondary); margin-bottom:12px; line-height:1.45;">
        ${escapeHtml(item.description)}
      </p>
      <div class="flex-between" style="margin-bottom:10px;">
        <div>
          <div class="price-highlight">$${Number(item.pricePerTonne).toFixed(2)} <span style="font-size:0.78rem; font-weight:600;">${escapeHtml(item.unit)}</span></div>
          <div style="font-size:0.78rem; color:var(--text-secondary);">⚖ ${Number(item.quantityTonnes).toFixed(0)} tonnes available</div>
        </div>
        <div style="text-align:right;">
          <div style="font-size:0.8rem; font-weight:700; color:var(--text-dark);">📍 ${escapeHtml(item.location)}</div>
          <div style="font-size:0.76rem; color:var(--text-muted);">
            ${item.sellerVerified ? '✔ ' : ''}${escapeHtml(item.sellerName)}
          </div>
        </div>
      </div>
      <div class="divider-line"></div>
      <div class="flex-between">
        <span style="font-size:0.78rem; color:var(--text-secondary);">🚚 ${escapeHtml(item.deliveryOptions)}</span>
        <span class="btn-white-pill" style="padding:6px 14px; font-size:0.78rem;">Inspect & Quote →</span>
      </div>
    </div>
  `;
}

function renderHomeScreen() {
  const listings = getAllListings();
  return `
    <section class="hero-banner">
      <div class="hero-top-row">
        <div style="display:flex; align-items:center; gap:10px;">
          <div class="brand-logo-box" style="width:36px; height:36px;">♻</div>
          <span style="font-weight:800; letter-spacing:0.08em;">NEXCORE</span>
        </div>
        <span class="hero-badge">Construction Waste. Renewed Value.</span>
      </div>
      <h1 class="hero-title">Give Waste a Second Life.</h1>
      <p class="hero-subtitle">
        Buy recycled construction materials, sell construction debris, or book mobile crushing services through one unified circular construction marketplace for Windows & Mobile.
      </p>
      <div class="hero-cta-row">
        <button class="btn-white-pill" onclick="navigateTo('BUY_MATERIALS', 'All', 'MATERIAL')">
          Browse Recycled Materials ↑
        </button>
        <button class="btn-secondary" onclick="navigateTo('BOOK_CRUSHER')">
          Book Mobile Crusher
        </button>
        <a class="btn-primary" href="https://github.com/shafffff699/Nextcore-apk/releases/latest/download/NEXCORE-debug.apk" target="_blank" rel="noopener">
          ⬇ Download Android APK
        </a>
      </div>
    </section>

    <section class="glass-panel">
      <div class="panel-header">
        <div>
          <h2 class="panel-title">Explore NEXCORE Marketplace</h2>
          <p class="panel-subtitle">Find graded recycled aggregates, list C&D debris, or schedule on-site mobile crushing</p>
        </div>
      </div>
      <div style="display:flex; flex-direction:column; gap:14px;">
        <div style="display:flex; gap:10px; flex-wrap:wrap;">
          <input
            type="text"
            class="glass-input"
            style="flex:1; min-width:240px;"
            placeholder="Search RCA 20mm, crushed brick, steel scrap, or site location..."
            value="${escapeHtml(filters.searchQuery)}"
            oninput="filters.searchQuery = this.value"
            onkeydown="if(event.key==='Enter') navigateTo('BUY_MATERIALS')"
          />
          <button class="btn-primary" onclick="navigateTo('BUY_MATERIALS')">Search Marketplace</button>
        </div>
        <div class="flex-wrap-chips">
          <button class="btn-primary" onclick="navigateTo('BUY_MATERIALS', 'All', 'MATERIAL')">🛒 Buy Materials</button>
          <button class="btn-secondary" onclick="navigateTo('SELL_WASTE')">➕ Sell Waste / Materials</button>
          <button class="glass-chip" onclick="navigateTo('BOOK_CRUSHER')">🏭 Book Mobile Crusher</button>
          <button class="glass-chip" onclick="navigateTo('BUY_MATERIALS', 'All', 'ALL')">📍 Explore Nearby Listings</button>
        </div>
      </div>
    </section>

    <section class="glass-panel">
      <div class="panel-header">
        <div>
          <h2 class="panel-title">Marketplace Categories</h2>
          <p class="panel-subtitle">Browse verified C&D materials and on-site processing in order</p>
        </div>
      </div>
      <div class="grid-categories">
        ${MATERIAL_CATEGORIES.map((cat, idx) => {
          const img = categoryImageUrl('', cat);
          const isCrusher = cat === 'Mobile Crushing Services';
          return `
            <div class="glass-card clickable" onclick="${isCrusher ? "navigateTo('BOOK_CRUSHER')" : `navigateTo('BUY_MATERIALS', '${cat}', 'ALL')`}">
              <div class="category-thumb">
                <img src="${img}" alt="${escapeHtml(cat)}" loading="lazy" />
              </div>
              <div style="font-size:0.72rem; font-weight:700; color:var(--primary-blue); margin-bottom:2px;">Category ${idx + 1}</div>
              <h3 style="font-size:0.98rem; font-weight:800; margin-bottom:6px;">${escapeHtml(cat)}</h3>
              <span style="font-size:0.8rem; font-weight:700; color:var(--primary-blue);">
                ${isCrusher ? 'Book Service →' : 'View Listings →'}
              </span>
            </div>
          `;
        }).join('')}
      </div>
    </section>

    <section class="glass-panel">
      <div class="panel-header">
        <div>
          <h2 class="panel-title">Featured C&D Listings</h2>
          <p class="panel-subtitle">Verified recycled materials and segregated demolition debris ready for quote or pickup</p>
        </div>
        <button class="glass-chip active" onclick="navigateTo('BUY_MATERIALS', 'All', 'ALL')">
          View All (${listings.length}) →
        </button>
      </div>
      <div class="grid-2">
        ${listings.slice(0, 4).map(renderListingCard).join('')}
      </div>
    </section>

    <section class="glass-panel">
      <div class="panel-header">
        <div>
          <h2 class="panel-title">How NEXCORE Works</h2>
          <p class="panel-subtitle">Four streamlined steps from demolition site debris to renewed engineering value</p>
        </div>
      </div>
      <div class="grid-2">
        ${[
          ['Step 1', 'List Waste or Search Materials', 'List construction waste from your site or search verified recycled aggregates by sieve spec and location.'],
          ['Step 2', 'Connect Across the Circular Supply Chain', 'Connect directly with contractors, aggregate buyers, waste sellers, or certified recycling service providers.'],
          ['Step 3', 'Request Quotations or Book Mobile Crushing', 'Request transparent itemized material quotations or book mobile jaw/impact crushing at your site.'],
          ['Step 4', 'Recover & Dispatch Renewed Materials', 'Process suitable debris close to generation and connect recovered aggregates with nearby construction buyers.']
        ].map(([step, title, desc], i) => `
          <div class="glass-card ${i === 0 ? 'iridescent' : ''}">
            <div style="display:flex; gap:12px; align-items:flex-start;">
              <div style="width:36px; height:36px; border-radius:50%; background:linear-gradient(135deg, var(--primary-purple), var(--primary-blue)); color:#fff; font-weight:800; display:flex; align-items:center; justify-content:center; flex-shrink:0;">
                ${i + 1}
              </div>
              <div>
                <h3 style="font-size:0.98rem; font-weight:800; margin-bottom:4px;">${step} — ${title}</h3>
                <p style="font-size:0.84rem; color:var(--text-secondary); line-height:1.45;">${desc}</p>
              </div>
            </div>
          </div>
        `).join('')}
      </div>
    </section>
  `;
}

function renderBuyMaterialsScreen() {
  if (state.selectedListingId) {
    const item = getAllListings().find(l => l.id === state.selectedListingId);
    if (item) return renderListingDetailView(item);
  }

  const filtered = getFilteredListings();
  const categories = ['All', ...MATERIAL_CATEGORIES.filter(c => c !== 'Mobile Crushing Services')];
  const sortOptions = ['Recently Listed', 'Lowest Price', 'Highest Price', 'Highest Quantity'];

  return `
    <section class="glass-panel">
      <div class="panel-header">
        <div>
          <h2 class="panel-title">Buy Recycled Materials & Browse Debris</h2>
          <p class="panel-subtitle">Filter verified C&D aggregates, crushed concrete, brick sub-base, road-base, and scrap metal</p>
        </div>
        <button class="glass-chip" onclick="resetFilters()">↺ Reset Filters</button>
      </div>

      <div style="display:flex; flex-direction:column; gap:14px;">
        <input
          type="text"
          class="glass-input"
          placeholder="Search by grade (20mm, Type 1), material, or supplier..."
          value="${escapeHtml(filters.searchQuery)}"
          oninput="filters.searchQuery = this.value; updateBuyResults()"
        />

        <div>
          <div class="glass-label" style="margin-bottom:6px;">Listing Type</div>
          <div class="flex-wrap-chips">
            ${[
              ['ALL', 'All Items'],
              ['MATERIAL', 'Recycled Materials'],
              ['WASTE', 'Raw C&D Debris']
            ].map(([code, label]) => `
              <button class="glass-chip ${filters.listingType === code ? 'active' : ''}" onclick="filters.listingType='${code}'; renderApp()">
                ${label}
              </button>
            `).join('')}
          </div>
        </div>

        <div>
          <div class="glass-label" style="margin-bottom:6px;">Material Category</div>
          <div class="flex-wrap-chips">
            ${categories.map(cat => `
              <button class="glass-chip ${filters.category.toLowerCase() === cat.toLowerCase() ? 'active' : ''}" onclick="filters.category='${cat}'; renderApp()">
                ${escapeHtml(cat)}
              </button>
            `).join('')}
          </div>
        </div>

        <div class="grid-2">
          <div class="glass-input-group">
            <label class="glass-label">Max Price: $${filters.maxPrice}/tonne</label>
            <input type="range" min="5" max="300" value="${filters.maxPrice}" oninput="filters.maxPrice=Number(this.value); renderApp()" />
          </div>
          <div class="glass-input-group">
            <label class="glass-label">Min Quantity: ${filters.minQty} tonnes</label>
            <input type="range" min="0" max="500" step="10" value="${filters.minQty}" oninput="filters.minQty=Number(this.value); renderApp()" />
          </div>
        </div>

        <div class="grid-2">
          <div class="glass-input-group">
            <label class="glass-label">Filter by Location / Yard / Sector</label>
            <input
              type="text"
              class="glass-input"
              placeholder="e.g., North Industrial, Docklands, Harbor..."
              value="${escapeHtml(filters.location)}"
              oninput="filters.location = this.value; updateBuyResults()"
            />
          </div>
          <div class="glass-input-group">
            <label class="glass-label">Sort Listings By</label>
            <div class="flex-wrap-chips">
              ${sortOptions.map(s => `
                <button class="glass-chip ${filters.sortBy === s ? 'active' : ''}" onclick="filters.sortBy='${s}'; renderApp()">
                  ${s}
                </button>
              `).join('')}
            </div>
          </div>
        </div>
      </div>
    </section>

    <section class="glass-panel" id="buy-results-panel">
      ${renderBuyResultsInner(filtered)}
    </section>
  `;
}

function renderBuyResultsInner(filtered) {
  return `
    <div class="panel-header">
      <div>
        <h2 class="panel-title">Available Marketplace Listings (${filtered.length})</h2>
        <p class="panel-subtitle">Click or tap any material card to inspect sieve specifications and request a formal quotation</p>
      </div>
    </div>
    ${filtered.length === 0 ? `
      <div class="glass-card" style="text-align:center; padding:32px;">
        <h3 style="margin-bottom:8px;">No listings match your current filters.</h3>
        <p style="color:var(--text-secondary); margin-bottom:16px;">Try expanding your maximum price per tonne or clearing location/category filters.</p>
        <button class="btn-primary" onclick="resetFilters()">Reset All Filters</button>
      </div>
    ` : `
      <div class="grid-2">
        ${filtered.map(renderListingCard).join('')}
      </div>
    `}
  `;
}

window.updateBuyResults = function() {
  const panel = document.getElementById('buy-results-panel');
  if (panel) {
    panel.innerHTML = renderBuyResultsInner(getFilteredListings());
  }
};

window.resetFilters = function() {
  filters.searchQuery = '';
  filters.category = 'All';
  filters.listingType = 'ALL';
  filters.maxPrice = 300;
  filters.minQty = 0;
  filters.location = '';
  filters.sortBy = 'Recently Listed';
  renderApp();
};

function renderListingDetailView(item) {
  const isSaved = (state.userProfile.savedListingIds || []).includes(item.id);
  const defaultQty = Math.min(50, item.quantityTonnes);
  const subtotal = defaultQty * item.pricePerTonne;
  const transport = item.transportIncluded ? 0 : Math.max(95, defaultQty * 3.25);
  const processing = Math.max(25, subtotal * 0.025);
  const tax = (subtotal + transport + processing) * 0.08;
  const total = subtotal + transport + processing + tax;

  return `
    <div class="flex-between">
      <button class="btn-white-pill" onclick="closeListingDetail()">← Back to Listings</button>
      <button class="glass-chip ${isSaved ? 'active' : ''}" onclick="toggleSaveListing(event, '${item.id}')">
        ${isSaved ? '★ Saved in Bookmarks' : '☆ Save Listing'}
      </button>
    </div>

    <div class="grid-2" style="align-items:start;">
      <section class="glass-panel">
        <div class="panel-header">
          <div>
            <h2 class="panel-title">${escapeHtml(item.title)}</h2>
            <p class="panel-subtitle">${escapeHtml(item.category)} • Listed by ${escapeHtml(item.sellerName)}</p>
          </div>
        </div>

        ${renderMaterialBanner(item)}

        <div class="flex-wrap-chips" style="margin-bottom:14px;">
          <span class="${statusBadgeClass(item.status)}">${escapeHtml(item.status)}</span>
          <span class="${statusBadgeClass(item.priceType)}">${escapeHtml(item.priceType)}</span>
          <span class="${statusBadgeClass('Active')}">${item.listingType === 'WASTE' ? 'Construction Debris' : 'Recycled Aggregate'}</span>
        </div>

        <div class="glass-card" style="margin-bottom:14px;">
          <div class="flex-between">
            <div>
              <div style="font-size:0.78rem; color:var(--text-secondary);">Indicative Unit Price</div>
              <div class="price-highlight" style="font-size:1.45rem;">$${Number(item.pricePerTonne).toFixed(2)} ${escapeHtml(item.unit)}</div>
            </div>
            <div style="text-align:right;">
              <div style="font-size:0.78rem; color:var(--text-secondary);">Available Stock</div>
              <div style="font-size:1.15rem; font-weight:800;">${Number(item.quantityTonnes).toFixed(0)} Tonnes</div>
            </div>
          </div>
        </div>

        <h3 style="font-size:0.95rem; font-weight:800; margin-bottom:6px;">Material Overview</h3>
        <p style="font-size:0.9rem; color:var(--text-secondary); line-height:1.5; margin-bottom:14px;">
          ${escapeHtml(item.description)}
        </p>

        <h3 style="font-size:0.95rem; font-weight:800; margin-bottom:6px;">Grading & Contamination Specifications</h3>
        <div class="glass-card" style="margin-bottom:14px; font-size:0.86rem;">
          ${escapeHtml(item.conditionSpecs || 'Standard uncontaminated C&D specification.')}
        </div>

        <div class="grid-2">
          <div class="glass-card">
            <div style="font-size:0.76rem; color:var(--text-secondary);">📍 Site / Yard Location</div>
            <div style="font-size:0.88rem; font-weight:700; margin-top:4px;">${escapeHtml(item.location)}</div>
          </div>
          <div class="glass-card">
            <div style="font-size:0.76rem; color:var(--text-secondary);">🚚 Logistics & Dispatch</div>
            <div style="font-size:0.88rem; font-weight:700; margin-top:4px;">${escapeHtml(item.deliveryOptions)} (${escapeHtml(item.preferredCollectionDate)})</div>
          </div>
        </div>
      </section>

      <section class="glass-panel">
        <div class="panel-header">
          <div>
            <h2 class="panel-title">Request Official Quotation (RFQ)</h2>
            <p class="panel-subtitle">Instant itemized estimate with direct seller quote confirmation</p>
          </div>
        </div>

        <div style="display:flex; flex-direction:column; gap:14px;">
          <div class="glass-input-group">
            <label class="glass-label">Required Quantity (Tonnes)</label>
            <input
              type="number"
              id="rfq-qty-input"
              class="glass-input"
              value="${defaultQty}"
              min="1"
              max="${item.quantityTonnes}"
              oninput="recalculateRfq('${item.id}')"
            />
          </div>

          <div class="glass-input-group">
            <label class="glass-label">Delivery / Collection Preference</label>
            <select id="rfq-delivery-select" class="glass-select" onchange="recalculateRfq('${item.id}')">
              <option value="Site Delivery Required">Site Delivery Required</option>
              <option value="Buyer Self-Pickup">Buyer Self-Pickup (Zero Haulage Fee)</option>
              <option value="NEXCORE Logistics Hauler">NEXCORE Logistics Hauler</option>
            </select>
          </div>

          <div class="glass-input-group">
            <label class="glass-label">Delivery Address, Unloading Access & Notes</label>
            <textarea id="rfq-message-input" class="glass-textarea">Please include haulage estimate and earliest dispatch window.</textarea>
          </div>

          <div class="glass-card iridescent" id="rfq-summary-box">
            <h3 style="font-size:0.95rem; font-weight:800; margin-bottom:8px;">Estimated Quotation Summary (${defaultQty} t)</h3>
            <div class="flex-between" style="font-size:0.84rem; margin-bottom:4px;">
              <span>Material Subtotal</span>
              <strong>$${subtotal.toFixed(2)}</strong>
            </div>
            <div class="flex-between" style="font-size:0.84rem; margin-bottom:4px;">
              <span>Estimated Haulage / Transport</span>
              <strong>$${transport.toFixed(2)}</strong>
            </div>
            <div class="flex-between" style="font-size:0.84rem; margin-bottom:4px;">
              <span>Platform & Quality Processing Fee</span>
              <strong>$${processing.toFixed(2)}</strong>
            </div>
            <div class="flex-between" style="font-size:0.84rem; margin-bottom:8px;">
              <span>Estimated Tax (8%)</span>
              <strong>$${tax.toFixed(2)}</strong>
            </div>
            <div class="divider-line"></div>
            <div class="flex-between" style="font-size:1.05rem; font-weight:800;">
              <span>Total Estimated Order Value</span>
              <span class="price-highlight">$${total.toFixed(2)}</span>
            </div>
          </div>

          <button class="btn-primary" style="width:100%;" onclick="submitRfqOrder('${item.id}')">
            Submit Quotation Request
          </button>
        </div>
      </section>
    </div>
  `;
}

window.recalculateRfq = function(listingId) {
  const item = getAllListings().find(l => l.id === listingId);
  if (!item) return;
  const qtyEl = document.getElementById('rfq-qty-input');
  const delEl = document.getElementById('rfq-delivery-select');
  const box = document.getElementById('rfq-summary-box');
  if (!qtyEl || !delEl || !box) return;

  const qty = Math.max(1, Number(qtyEl.value) || 1);
  const deliv = delEl.value;
  const subtotal = qty * item.pricePerTonne;
  const transport = (item.transportIncluded || deliv.includes('Self-Pickup')) ? 0 : Math.max(95, qty * 3.25);
  const processing = Math.max(25, subtotal * 0.025);
  const tax = (subtotal + transport + processing) * 0.08;
  const total = subtotal + transport + processing + tax;

  box.innerHTML = `
    <h3 style="font-size:0.95rem; font-weight:800; margin-bottom:8px;">Estimated Quotation Summary (${qty} t)</h3>
    <div class="flex-between" style="font-size:0.84rem; margin-bottom:4px;">
      <span>Material Subtotal</span>
      <strong>$${subtotal.toFixed(2)}</strong>
    </div>
    <div class="flex-between" style="font-size:0.84rem; margin-bottom:4px;">
      <span>Estimated Haulage / Transport</span>
      <strong>$${transport.toFixed(2)}</strong>
    </div>
    <div class="flex-between" style="font-size:0.84rem; margin-bottom:4px;">
      <span>Platform & Quality Processing Fee</span>
      <strong>$${processing.toFixed(2)}</strong>
    </div>
    <div class="flex-between" style="font-size:0.84rem; margin-bottom:8px;">
      <span>Estimated Tax (8%)</span>
      <strong>$${tax.toFixed(2)}</strong>
    </div>
    <div class="divider-line"></div>
    <div class="flex-between" style="font-size:1.05rem; font-weight:800;">
      <span>Total Estimated Order Value</span>
      <span class="price-highlight">$${total.toFixed(2)}</span>
    </div>
  `;
};

window.submitRfqOrder = function(listingId) {
  const item = getAllListings().find(l => l.id === listingId);
  if (!item) return;
  const qty = Math.max(1, Number(document.getElementById('rfq-qty-input')?.value) || 25);
  const deliv = document.getElementById('rfq-delivery-select')?.value || 'Site Delivery Required';
  const subtotal = qty * item.pricePerTonne;
  const transport = (item.transportIncluded || deliv.includes('Self-Pickup')) ? 0 : Math.max(95, qty * 3.25);
  const processing = Math.max(25, subtotal * 0.025);
  const tax = (subtotal + transport + processing) * 0.08;
  const total = subtotal + transport + processing + tax;

  const ref = 'RFQ-' + Math.floor(10000 + Math.random() * 90000);
  state.quotations.unshift({
    id: 'q_' + Date.now(),
    orderRef: ref,
    listingId: item.id,
    listingTitle: item.title,
    category: item.category,
    sellerName: item.sellerName,
    quantityTonnes: qty,
    unitPrice: item.pricePerTonne,
    totalAmount: total,
    deliveryPreference: deliv,
    orderStatus: 'Quote Requested',
    paymentStatus: 'Payment Pending',
    createdAt: 'Just now'
  });
  saveState();
  state.selectedListingId = null;
  state.currentTab = 'TRACK_ORDERS';
  showBanner(`Submitted Quotation Request ${ref} for ${qty} tonnes of ${item.title}.`);
};

function renderSellWasteScreen() {
  const availableCategories = sellDraft.modeIndex === 0
    ? WASTE_CATEGORIES
    : MATERIAL_CATEGORIES.filter(c => c !== 'Mobile Crushing Services');

  return `
    <section class="hero-banner">
      <div class="hero-top-row">
        <span class="hero-badge">Post a Listing</span>
      </div>
      <h1 class="hero-title">Turn Demolition Debris Into Revenue</h1>
      <p class="hero-subtitle">Post segregated concrete, brick rubble, or processed recycled aggregates directly to verified regional buyers.</p>
      <button class="btn-white-pill" onclick="navigateTo('BOOK_CRUSHER')">Need On-Site Crushing First? →</button>
    </section>

    <section class="glass-panel">
      <div class="panel-header">
        <div>
          <h2 class="panel-title">Create Marketplace Listing</h2>
          <p class="panel-subtitle">Choose whether you are listing raw site debris or processed recycled materials</p>
        </div>
      </div>

      <div style="display:flex; flex-direction:column; gap:16px;">
        <div class="segmented-tabs">
          <div class="segmented-tab ${sellDraft.modeIndex === 0 ? 'active' : ''}" onclick="setSellMode(0)">
            Sell C&D Waste / Debris
          </div>
          <div class="segmented-tab ${sellDraft.modeIndex === 1 ? 'active' : ''}" onclick="setSellMode(1)">
            Sell Recycled Material
          </div>
        </div>

        ${renderMaterialBanner({
          imageKey: sellDraft.imageKey,
          category: sellDraft.category,
          isSample: false
        })}

        <div>
          <div class="glass-label" style="margin-bottom:6px;">Visual Spec Banner Theme (In Exact Category Order)</div>
          <div class="flex-wrap-chips">
            ${IMAGE_OPTIONS.map(opt => `
              <button class="glass-chip ${sellDraft.imageKey === opt.key ? 'active' : ''}" onclick="sellDraft.imageKey='${opt.key}'; renderApp()">
                ${escapeHtml(opt.label)}
              </button>
            `).join('')}
          </div>
        </div>

        <div class="glass-input-group">
          <label class="glass-label">Listing Title *</label>
          <input
            type="text"
            class="glass-input"
            placeholder="e.g., 300T Segregated Structural Concrete Slab Rubble (<400mm)"
            value="${escapeHtml(sellDraft.title)}"
            oninput="sellDraft.title = this.value"
          />
        </div>

        <div>
          <div class="glass-label" style="margin-bottom:6px;">Category *</div>
          <div class="flex-wrap-chips">
            ${availableCategories.map(cat => `
              <button class="glass-chip ${sellDraft.category === cat ? 'active' : ''}" onclick="selectSellCategory('${cat}')">
                ${escapeHtml(cat)}
              </button>
            `).join('')}
          </div>
        </div>

        <div class="grid-2">
          <div class="glass-input-group">
            <label class="glass-label">Quantity (Tonnes) *</label>
            <input type="number" class="glass-input" value="${escapeHtml(sellDraft.quantity)}" oninput="sellDraft.quantity = this.value" />
          </div>
          <div class="glass-input-group">
            <label class="glass-label">Price / Tonne (USD) *</label>
            <input type="number" step="0.5" class="glass-input" value="${escapeHtml(sellDraft.price)}" oninput="sellDraft.price = this.value" />
          </div>
        </div>

        <div class="grid-2">
          <div class="glass-input-group">
            <label class="glass-label">Site / Yard Location *</label>
            <input
              type="text"
              class="glass-input"
              placeholder="e.g., South Industrial Park, Gate 3"
              value="${escapeHtml(sellDraft.location)}"
              oninput="sellDraft.location = this.value"
            />
          </div>
          <div class="glass-input-group">
            <label class="glass-label">Availability / Collection Window</label>
            <input
              type="text"
              class="glass-input"
              value="${escapeHtml(sellDraft.preferredDate)}"
              oninput="sellDraft.preferredDate = this.value"
            />
          </div>
        </div>

        <div class="glass-input-group">
          <label class="glass-label">Grading, Particle Size & Contamination Specs</label>
          <textarea class="glass-textarea" oninput="sellDraft.conditionSpecs = this.value">${escapeHtml(sellDraft.conditionSpecs)}</textarea>
        </div>

        <button class="btn-primary" style="width:100%;" onclick="publishListing()">
          Publish Listing to NEXCORE Marketplace
        </button>
      </div>
    </section>
  `;
}

window.setSellMode = function(idx) {
  sellDraft.modeIndex = idx;
  sellDraft.category = idx === 0 ? WASTE_CATEGORIES[0] : MATERIAL_CATEGORIES[0];
  sellDraft.imageKey = imageKeyForCategory(sellDraft.category);
  renderApp();
};

window.selectSellCategory = function(cat) {
  sellDraft.category = cat;
  sellDraft.imageKey = imageKeyForCategory(cat);
  renderApp();
};

window.publishListing = function() {
  const title = sellDraft.title.trim();
  const loc = sellDraft.location.trim();
  const qty = Number(sellDraft.quantity) || 0;
  const price = Number(sellDraft.price);

  if (title.length < 4) {
    showBanner('Please enter a listing title of at least 4 characters.');
    return;
  }
  if (!loc) {
    showBanner('Please enter the site or yard location.');
    return;
  }
  if (qty <= 0 || isNaN(price) || price < 0) {
    showBanner('Please enter valid tonnage (>0) and price per tonne.');
    return;
  }

  const newListing = {
    id: 'listing_' + Date.now(),
    sellerName: state.userProfile.companyName || state.userProfile.fullName,
    sellerRole: state.userProfile.accountType,
    sellerVerified: true,
    listingType: sellDraft.modeIndex === 0 ? 'WASTE' : 'MATERIAL',
    title,
    category: sellDraft.category,
    description: sellDraft.description.trim() || `Verified ${sellDraft.category} available at ${loc}.`,
    conditionSpecs: sellDraft.conditionSpecs.trim(),
    quantityTonnes: qty,
    pricePerTonne: price,
    unit: 'per tonne',
    priceType: sellDraft.priceType,
    location: loc,
    preferredCollectionDate: sellDraft.preferredDate,
    deliveryOptions: sellDraft.deliveryOptions,
    transportIncluded: sellDraft.transportIncluded,
    contactPreference: 'Platform Quote',
    status: 'Active',
    isSample: false,
    imageKey: sellDraft.imageKey,
    createdAt: Date.now()
  };

  state.customListings.unshift(newListing);
  sellDraft.title = '';
  sellDraft.location = '';
  saveState();
  filters.category = 'All';
  filters.listingType = 'ALL';
  state.currentTab = 'BUY_MATERIALS';
  showBanner(`Published "${newListing.title}" to the live NEXCORE Marketplace!`);
};

function renderBookCrusherScreen() {
  const tonnes = Math.max(50, Number(crusherDraft.estimatedTonnes) || 350);
  const mobilization = 850;
  const rate = crusherDraft.serviceType === 'Primary Jaw Crushing Only'
    ? 6.50
    : crusherDraft.serviceType === 'Multi-Deck Screening Only'
    ? 4.25
    : 8.75;
  const estimate = mobilization + tonnes * rate;

  return `
    <section class="glass-panel">
      <div class="panel-header">
        <div>
          <h2 class="panel-title">Book On-Site Mobile Crusher & Screener</h2>
          <p class="panel-subtitle">Deploy tracked jaw/impact crushers and multi-deck screeners directly to your demolition site</p>
        </div>
      </div>

      <div style="display:flex; flex-direction:column; gap:16px;">
        ${renderMaterialBanner({
          imageKey: 'mobile_crusher',
          category: 'Mobile Crushing Services',
          isSample: false
        })}

        <div class="glass-card iridescent">
          <div class="flex-between">
            <div>
              <div style="font-size:0.78rem; color:var(--text-secondary);">Estimated Mobilization & Processing Cost</div>
              <div class="price-highlight" style="font-size:1.5rem;">$${estimate.toFixed(2)}</div>
              <div style="font-size:0.8rem; color:var(--text-secondary);">
                Includes $850 mobilization + $${rate.toFixed(2)}/t (${tonnes} tonnes)
              </div>
            </div>
            <span class="${statusBadgeClass('On-Site Service')}">On-Site Service</span>
          </div>
        </div>

        <div class="grid-2">
          <div class="glass-input-group">
            <label class="glass-label">Contact / Site Manager *</label>
            <input type="text" class="glass-input" value="${escapeHtml(crusherDraft.customerName)}" oninput="crusherDraft.customerName = this.value" />
          </div>
          <div class="glass-input-group">
            <label class="glass-label">Site Phone *</label>
            <input type="text" class="glass-input" value="${escapeHtml(crusherDraft.contactNumber)}" oninput="crusherDraft.contactNumber = this.value" />
          </div>
        </div>

        <div class="glass-input-group">
          <label class="glass-label">Project / Demolition Site Location *</label>
          <input type="text" class="glass-input" value="${escapeHtml(crusherDraft.siteLocation)}" oninput="crusherDraft.siteLocation = this.value" />
        </div>

        <div>
          <div class="glass-label" style="margin-bottom:6px;">Debris / Feedstock Material Type *</div>
          <div class="flex-wrap-chips">
            ${WASTE_CATEGORIES.map(wt => `
              <button class="glass-chip ${crusherDraft.wasteType === wt ? 'active' : ''}" onclick="crusherDraft.wasteType='${wt}'; renderApp()">
                ${escapeHtml(wt)}
              </button>
            `).join('')}
          </div>
        </div>

        <div>
          <div class="glass-label" style="margin-bottom:6px;">Equipment Configuration</div>
          <div class="flex-wrap-chips">
            ${[
              'Crushing & Multi-Deck Screening',
              'Primary Jaw Crushing Only',
              'Multi-Deck Screening Only'
            ].map(st => `
              <button class="glass-chip ${crusherDraft.serviceType === st ? 'active' : ''}" onclick="crusherDraft.serviceType='${st}'; renderApp()">
                ${st}
              </button>
            `).join('')}
          </div>
        </div>

        <div class="grid-2">
          <div class="glass-input-group">
            <label class="glass-label">Est. Debris Volume (Tonnes) *</label>
            <input type="number" class="glass-input" value="${escapeHtml(crusherDraft.estimatedTonnes)}" oninput="crusherDraft.estimatedTonnes = this.value; renderApp()" />
          </div>
          <div class="glass-input-group">
            <label class="glass-label">Preferred Mobilization Date *</label>
            <input type="text" class="glass-input" value="${escapeHtml(crusherDraft.preferredDate)}" oninput="crusherDraft.preferredDate = this.value" />
          </div>
        </div>

        <button class="btn-primary" style="width:100%;" onclick="submitCrusherBooking()">
          Submit Mobile Crusher Booking
        </button>
      </div>
    </section>
  `;
}

window.submitCrusherBooking = function() {
  if (!crusherDraft.customerName.trim() || !crusherDraft.siteLocation.trim()) {
    showBanner('Please enter the site manager name and demolition site address.');
    return;
  }
  const tonnes = Math.max(50, Number(crusherDraft.estimatedTonnes) || 350);
  const rate = crusherDraft.serviceType === 'Primary Jaw Crushing Only' ? 6.50 : crusherDraft.serviceType === 'Multi-Deck Screening Only' ? 4.25 : 8.75;
  const quote = 850 + tonnes * rate;
  const ref = 'CRB-' + Math.floor(10000 + Math.random() * 90000);

  state.crusherBookings.unshift({
    id: 'cb_' + Date.now(),
    bookingRef: ref,
    customerName: crusherDraft.customerName.trim(),
    contactNumber: crusherDraft.contactNumber.trim(),
    siteLocation: crusherDraft.siteLocation.trim(),
    wasteType: crusherDraft.wasteType,
    estimatedTonnes: tonnes,
    serviceType: crusherDraft.serviceType,
    preferredDate: crusherDraft.preferredDate.trim() || 'Flexible',
    quotedAmount: quote,
    status: 'Request Submitted'
  });
  saveState();
  state.currentTab = 'TRACK_ORDERS';
  showBanner(`Mobile Crusher Booking ${ref} submitted for ${tonnes} tonnes at ${crusherDraft.siteLocation}!`);
};

function renderTrackOrdersScreen() {
  return `
    <section class="glass-panel">
      <div class="panel-header">
        <div>
          <h2 class="panel-title">Material Quotation Orders (${state.quotations.length})</h2>
          <p class="panel-subtitle">Track RFQ status, haulage dispatch, and delivery confirmations</p>
        </div>
        <button class="glass-chip" onclick="navigateTo('BUY_MATERIALS')">+ New Material RFQ</button>
      </div>

      <div class="grid-2">
        ${state.quotations.map(q => `
          <div class="glass-card">
            <div class="flex-between" style="margin-bottom:8px;">
              <strong>${escapeHtml(q.orderRef)}</strong>
              <span class="${statusBadgeClass(q.orderStatus)}">${escapeHtml(q.orderStatus)}</span>
            </div>
            <h3 style="font-size:1rem; font-weight:800; margin-bottom:4px;">${escapeHtml(q.listingTitle)}</h3>
            <p style="font-size:0.82rem; color:var(--text-secondary); margin-bottom:10px;">
              Supplier: ${escapeHtml(q.sellerName)} • ${q.quantityTonnes} tonnes • ${escapeHtml(q.deliveryPreference)}
            </p>
            <div class="flex-between">
              <span class="price-highlight">$${Number(q.totalAmount).toFixed(2)}</span>
              <button class="glass-chip active" onclick="advanceQuotationStatus('${q.id}')">
                Advance Status →
              </button>
            </div>
          </div>
        `).join('')}
      </div>
    </section>

    <section class="glass-panel">
      <div class="panel-header">
        <div>
          <h2 class="panel-title">Mobile Crusher Bookings (${state.crusherBookings.length})</h2>
          <p class="panel-subtitle">Live equipment mobilization and on-site crushing progress</p>
        </div>
        <button class="glass-chip" onclick="navigateTo('BOOK_CRUSHER')">+ Book Crusher</button>
      </div>

      <div class="grid-2">
        ${state.crusherBookings.map(b => `
          <div class="glass-card">
            <div class="flex-between" style="margin-bottom:8px;">
              <strong>${escapeHtml(b.bookingRef)}</strong>
              <span class="${statusBadgeClass(b.status)}">${escapeHtml(b.status)}</span>
            </div>
            <h3 style="font-size:1rem; font-weight:800; margin-bottom:4px;">${escapeHtml(b.wasteType)} (${b.estimatedTonnes} tonnes)</h3>
            <p style="font-size:0.82rem; color:var(--text-secondary); margin-bottom:10px;">
              📍 ${escapeHtml(b.siteLocation)} • ⚙ ${escapeHtml(b.serviceType)} • 📅 ${escapeHtml(b.preferredDate)}
            </p>
            <div class="flex-between">
              <span class="price-highlight">$${Number(b.quotedAmount).toFixed(2)}</span>
              <button class="glass-chip active" onclick="advanceBookingStatus('${b.id}')">
                Advance Stage →
              </button>
            </div>
          </div>
        `).join('')}
      </div>
    </section>
  `;
}

window.advanceQuotationStatus = function(id) {
  const stages = ['Quote Requested', 'Quote Received', 'Accepted', 'Dispatched', 'Delivered'];
  const q = state.quotations.find(x => x.id === id);
  if (!q) return;
  const idx = stages.indexOf(q.orderStatus);
  q.orderStatus = stages[(idx + 1) % stages.length];
  saveState();
  renderApp();
};

window.advanceBookingStatus = function(id) {
  const stages = ['Request Submitted', 'Quotation Sent', 'Accepted', 'Scheduled', 'In Progress', 'Completed'];
  const b = state.crusherBookings.find(x => x.id === id);
  if (!b) return;
  const idx = stages.indexOf(b.status);
  b.status = stages[(idx + 1) % stages.length];
  saveState();
  renderApp();
};

function renderDashboardScreen() {
  const p = state.userProfile;
  const savedListings = getAllListings().filter(l => (p.savedListingIds || []).includes(l.id));
  const roles = [
    'Buyer / Customer',
    'Demolition Contractor / Waste Seller',
    'Recycling Service Provider'
  ];

  return `
    <div class="grid-2" style="align-items:start;">
      <section class="glass-panel">
        <div class="panel-header">
          <div>
            <h2 class="panel-title">Account & Circular Role Profile</h2>
            <p class="panel-subtitle">Manage your company details and marketplace role</p>
          </div>
          <span class="${statusBadgeClass('Active')}">✔ Verified Account</span>
        </div>

        <div style="display:flex; flex-direction:column; gap:12px;">
          <div>
            <div class="glass-label" style="margin-bottom:6px;">Active Marketplace Role</div>
            <div class="flex-wrap-chips">
              ${roles.map(r => `
                <button class="glass-chip ${p.accountType === r ? 'active' : ''}" onclick="state.userProfile.accountType='${r}'; saveState(); renderApp()">
                  ${r}
                </button>
              `).join('')}
            </div>
          </div>

          <div class="glass-input-group">
            <label class="glass-label">Full Name / Contact Person</label>
            <input type="text" class="glass-input" value="${escapeHtml(p.fullName)}" oninput="state.userProfile.fullName=this.value; saveState()" />
          </div>

          <div class="glass-input-group">
            <label class="glass-label">Company / Organization</label>
            <input type="text" class="glass-input" value="${escapeHtml(p.companyName)}" oninput="state.userProfile.companyName=this.value; saveState()" />
          </div>

          <div class="grid-2">
            <div class="glass-input-group">
              <label class="glass-label">Phone</label>
              <input type="text" class="glass-input" value="${escapeHtml(p.phone)}" oninput="state.userProfile.phone=this.value; saveState()" />
            </div>
            <div class="glass-input-group">
              <label class="glass-label">Primary City / Hub</label>
              <input type="text" class="glass-input" value="${escapeHtml(p.city)}" oninput="state.userProfile.city=this.value; saveState()" />
            </div>
          </div>

          <button class="btn-primary" onclick="showBanner('Account profile saved.')">Save Profile Changes</button>
        </div>
      </section>

      <section class="glass-panel">
        <div class="panel-header">
          <div>
            <h2 class="panel-title">Saved Listings (${savedListings.length}) & Mobile APK</h2>
            <p class="panel-subtitle">Quick access to bookmarked materials and native Android package</p>
          </div>
        </div>

        <div class="glass-card iridescent" style="margin-bottom:16px;">
          <h3 style="font-size:1rem; font-weight:800; margin-bottom:6px;">📱 Native Android App (APK)</h3>
          <p style="font-size:0.84rem; color:var(--text-secondary); margin-bottom:12px;">
            Using an Android phone? You can install the native NEXCORE Android APK directly alongside this web application.
          </p>
          <a class="btn-primary" href="https://github.com/shafffff699/Nextcore-apk/releases/latest/download/NEXCORE-debug.apk" target="_blank" rel="noopener">
            ⬇ Download NEXCORE-debug.apk
          </a>
        </div>

        <div style="display:flex; flex-direction:column; gap:12px;">
          ${savedListings.length === 0 ? `
            <div class="glass-card">No saved listings yet. Tap ☆ Save on any material card.</div>
          ` : savedListings.map(renderListingCard).join('')}
        </div>
      </section>
    </div>
  `;
}

function escapeHtml(str) {
  return String(str ?? '')
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;')
    .replace(/'/g, '&#39;');
}

function renderApp() {
  const root = document.getElementById('app-root');
  if (!root) return;

  const navItems = [
    { id: 'HOME', label: 'Home', short: 'Home', icon: '🏠' },
    { id: 'BUY_MATERIALS', label: 'Buy Materials', short: 'Buy', icon: '🛒' },
    { id: 'SELL_WASTE', label: 'Sell Waste', short: 'Sell', icon: '♻' },
    { id: 'BOOK_CRUSHER', label: 'Book Crusher', short: 'Crusher', icon: '🏭' },
    { id: 'TRACK_ORDERS', label: 'Track Orders', short: 'Orders', icon: '🚚' },
    { id: 'DASHBOARD', label: 'Account', short: 'Account', icon: '👤' }
  ];

  let screenHtml = '';
  switch (state.currentTab) {
    case 'BUY_MATERIALS':
      screenHtml = renderBuyMaterialsScreen();
      break;
    case 'SELL_WASTE':
      screenHtml = renderSellWasteScreen();
      break;
    case 'BOOK_CRUSHER':
      screenHtml = renderBookCrusherScreen();
      break;
    case 'TRACK_ORDERS':
      screenHtml = renderTrackOrdersScreen();
      break;
    case 'DASHBOARD':
      screenHtml = renderDashboardScreen();
      break;
    default:
      screenHtml = renderHomeScreen();
      break;
  }

  root.innerHTML = `
    <header class="top-header">
      <div class="header-main-row">
        <div class="brand-cluster" onclick="navigateTo('HOME')">
          <div class="brand-logo-box">
            <span style="font-size:1.35rem;">♻</span>
          </div>
          <div>
            <div class="brand-title">NEXCORE</div>
            <div class="brand-tagline">Construction Waste. Renewed Value.</div>
          </div>
        </div>
        <div class="header-actions">
          <button class="btn-primary" style="padding:8px 16px; font-size:0.82rem;" onclick="navigateTo('SELL_WASTE')">
            + Post Listing
          </button>
        </div>
      </div>

      <nav class="nav-chips-row">
        ${navItems.map(item => `
          <button class="glass-chip ${state.currentTab === item.id ? 'active' : ''}" onclick="navigateTo('${item.id}')">
            <span>${item.icon}</span>
            <span>${item.label}</span>
          </button>
        `).join('')}
        <span class="${statusBadgeClass('Active')}">${escapeHtml(state.userProfile.accountType)}</span>
      </nav>
    </header>

    ${state.bannerMessage ? `
      <div class="toast-banner" onclick="dismissBanner()">
        <span>✨ ${escapeHtml(state.bannerMessage)}</span>
        <span class="status-badge status-purple" style="cursor:pointer;">Dismiss</span>
      </div>
    ` : ''}

    <main style="display:flex; flex-direction:column; gap:20px;">
      ${screenHtml}
    </main>

    <nav class="bottom-glass-nav">
      ${navItems.map(item => `
        <div class="bottom-nav-item ${state.currentTab === item.id ? 'active' : ''}" onclick="navigateTo('${item.id}')">
          <div class="bottom-nav-icon">${item.icon}</div>
          <span>${item.short}</span>
        </div>
      `).join('')}
    </nav>
  `;
}

document.addEventListener('DOMContentLoaded', renderApp);
