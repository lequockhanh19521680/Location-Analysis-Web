import Link from 'next/link'

export default function Home() {
  return (
    <main className="min-h-screen p-8">
      <div className="max-w-7xl mx-auto">
        <header className="text-center mb-12">
          <h1 className="text-5xl font-bold mb-4 bg-gradient-to-r from-primary-600 to-primary-900 bg-clip-text text-transparent">
            Location Score AI V4.0
          </h1>
          <p className="text-xl text-gray-600">
            Comprehensive Location Analysis System
          </p>
        </header>

        <div className="grid md:grid-cols-2 gap-8 mb-12">
          <div className="card">
            <h2 className="text-2xl font-semibold mb-4">🎯 Smart Analysis</h2>
            <p className="text-gray-600 mb-4">
              Analyze locations using 5 core pillars and 12+ sub-indicators powered by real-time data from Google Maps.
            </p>
            <ul className="list-disc list-inside text-gray-600 space-y-2">
              <li>Competition & Saturation Analysis</li>
              <li>Traffic & Accessibility Metrics</li>
              <li>Socio-Economic Profiling</li>
              <li>Infrastructure Assessment</li>
              <li>Legal & Macro Risk Detection</li>
            </ul>
          </div>

          <div className="card">
            <h2 className="text-2xl font-semibold mb-4">⚡ Key Features</h2>
            <ul className="list-disc list-inside text-gray-600 space-y-2">
              <li>Dynamic weight customization</li>
              <li>Real-time scoring (under 4 seconds)</li>
              <li>Critical risk alerts (NFR7 Logic)</li>
              <li>Interactive radar charts</li>
              <li>Multi-location comparison</li>
              <li>Detailed raw data transparency</li>
            </ul>
          </div>
        </div>

        <div className="text-center space-x-4">
          <Link href="/login" className="btn-primary inline-block">
            Get Started
          </Link>
          <Link href="/about" className="btn-secondary inline-block">
            Learn More
          </Link>
        </div>

        <div className="mt-16 grid md:grid-cols-3 gap-6">
          <div className="text-center p-6">
            <div className="text-4xl mb-2">🏪</div>
            <h3 className="font-semibold mb-2">F&B</h3>
            <p className="text-sm text-gray-600">
              Restaurant, Cafe, Food Service Analysis
            </p>
          </div>
          <div className="text-center p-6">
            <div className="text-4xl mb-2">🛍️</div>
            <h3 className="font-semibold mb-2">Retail</h3>
            <p className="text-sm text-gray-600">
              Store, Shopping Mall, Retail Location Analysis
            </p>
          </div>
          <div className="text-center p-6">
            <div className="text-4xl mb-2">💼</div>
            <h3 className="font-semibold mb-2">Service</h3>
            <p className="text-sm text-gray-600">
              Beauty Salon, Gym, Service Business Analysis
            </p>
          </div>
        </div>
      </div>
    </main>
  )
}
