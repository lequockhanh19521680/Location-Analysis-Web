export default function AboutPage() {
  return (
    <div className="min-h-screen p-8">
      <div className="max-w-4xl mx-auto">
        <h1 className="text-4xl font-bold mb-8">About Location Score AI V4.0</h1>
        
        <div className="space-y-8">
          <section className="card">
            <h2 className="text-2xl font-semibold mb-4">What is Location Score AI?</h2>
            <p className="text-gray-600 leading-relaxed">
              Location Score AI V4.0 is a comprehensive location analysis system that helps businesses 
              make data-driven decisions about where to establish their operations. Using advanced 
              algorithms and real-time data from Google Maps, we analyze locations across 5 core pillars 
              and over 12 sub-indicators to provide actionable insights.
            </p>
          </section>

          <section className="card">
            <h2 className="text-2xl font-semibold mb-4">The 5 Core Pillars</h2>
            <div className="space-y-4">
              <div>
                <h3 className="font-semibold text-lg mb-2">A. Competition & Saturation (25-35%)</h3>
                <p className="text-gray-600">
                  Analyzes competitor density, quality, market saturation, and competitive advantages 
                  to help you understand the competitive landscape.
                </p>
              </div>
              
              <div>
                <h3 className="font-semibold text-lg mb-2">B. Traffic & Accessibility (15-35%)</h3>
                <p className="text-gray-600">
                  Evaluates foot traffic, public transport access, vehicle accessibility, and 
                  location visibility to gauge customer reach potential.
                </p>
              </div>
              
              <div>
                <h3 className="font-semibold text-lg mb-2">C. Socio-Economic (20-40%)</h3>
                <p className="text-gray-600">
                  Assesses demographic alignment, population density, and income levels to ensure 
                  your target market is present in the area.
                </p>
              </div>
              
              <div>
                <h3 className="font-semibold text-lg mb-2">D. Infrastructure & Environment (10%)</h3>
                <p className="text-gray-600">
                  Examines parking availability, safety facilities, and aesthetic factors that 
                  impact customer experience and business operations.
                </p>
              </div>
              
              <div>
                <h3 className="font-semibold text-lg mb-2">E. Macro & Legal Risk (10-15%)</h3>
                <p className="text-gray-600">
                  Identifies urban planning risks, future development potential, and economic 
                  outlook to protect your investment from regulatory and market changes.
                </p>
              </div>
            </div>
          </section>

          <section className="card">
            <h2 className="text-2xl font-semibold mb-4">Critical Risk Alert System</h2>
            <p className="text-gray-600 mb-4">
              Our NFR7 logic automatically detects high-risk locations. When urban planning risk 
              exceeds critical thresholds, the system caps the total score and issues a red alert 
              with specific recommendations.
            </p>
            <div className="p-4 bg-red-50 border border-red-200 rounded-lg">
              <p className="text-red-800 font-semibold">⚠️ Example Alert</p>
              <p className="text-red-700 text-sm mt-2">
                High urban planning risk detected. Location may be subject to zoning changes or 
                redevelopment. Total score capped at 5.0/10.
              </p>
            </div>
          </section>

          <section className="card">
            <h2 className="text-2xl font-semibold mb-4">Supported Industries</h2>
            <div className="grid md:grid-cols-3 gap-4">
              <div className="text-center p-4 bg-gray-50 rounded-lg">
                <div className="text-4xl mb-2">🏪</div>
                <h3 className="font-semibold">F&B</h3>
                <p className="text-sm text-gray-600 mt-1">
                  Restaurants, Cafes, Food Services
                </p>
              </div>
              
              <div className="text-center p-4 bg-gray-50 rounded-lg">
                <div className="text-4xl mb-2">🛍️</div>
                <h3 className="font-semibold">Retail</h3>
                <p className="text-sm text-gray-600 mt-1">
                  Stores, Malls, Retail Outlets
                </p>
              </div>
              
              <div className="text-center p-4 bg-gray-50 rounded-lg">
                <div className="text-4xl mb-2">💼</div>
                <h3 className="font-semibold">Service</h3>
                <p className="text-sm text-gray-600 mt-1">
                  Salons, Gyms, Service Businesses
                </p>
              </div>
            </div>
          </section>

          <section className="card">
            <h2 className="text-2xl font-semibold mb-4">Technology Stack</h2>
            <ul className="list-disc list-inside text-gray-600 space-y-2">
              <li><strong>Frontend:</strong> Next.js 14 with TypeScript</li>
              <li><strong>Backend:</strong> Node.js/Express microservices</li>
              <li><strong>Database:</strong> PostgreSQL with Prisma ORM</li>
              <li><strong>APIs:</strong> Google Maps Platform (Places, Geocoding, Directions)</li>
              <li><strong>Authentication:</strong> JWT with OAuth 2.0 support</li>
              <li><strong>Deployment:</strong> Docker containerization</li>
            </ul>
          </section>

          <div className="text-center">
            <a href="/" className="btn-primary inline-block">
              Back to Home
            </a>
          </div>
        </div>
      </div>
    </div>
  )
}
