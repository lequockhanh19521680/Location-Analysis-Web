'use client';

import { useState } from 'react';
import { useSession } from 'next-auth/react';
import { useRouter } from 'next/navigation';

const CORE_SERVICE_URL = process.env.NEXT_PUBLIC_CORE_SERVICE_URL || 'http://localhost:3002';

export default function AnalyzePage() {
  const { data: session } = useSession();
  const router = useRouter();
  const [formData, setFormData] = useState({
    address: '',
    latitude: '',
    longitude: '',
    industry: 'F&B',
    industrySubType: '',
    radius: '500',
    weightA: '0.30',
    weightB: '0.25',
    weightC: '0.30',
    weightD: '0.10',
    weightE: '0.05',
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [result, setResult] = useState<any>(null);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    try {
      const response = await fetch(`${CORE_SERVICE_URL}/core/score`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${(session as any)?.accessToken}`,
        },
        body: JSON.stringify({
          address: formData.address,
          latitude: parseFloat(formData.latitude),
          longitude: parseFloat(formData.longitude),
          industry: formData.industry,
          industrySubType: formData.industrySubType || undefined,
          radius: parseInt(formData.radius),
          weights: {
            weightA: parseFloat(formData.weightA),
            weightB: parseFloat(formData.weightB),
            weightC: parseFloat(formData.weightC),
            weightD: parseFloat(formData.weightD),
            weightE: parseFloat(formData.weightE),
          },
        }),
      });

      const data = await response.json();

      if (response.ok) {
        setResult(data);
      } else {
        setError(data.error || 'Analysis failed');
      }
    } catch (err) {
      setError('An error occurred. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-gray-50">
      <nav className="bg-white shadow-sm">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex justify-between h-16 items-center">
            <h1 className="text-xl font-bold text-gray-900">
              Location Score AI
            </h1>
            <button
              onClick={() => router.push('/dashboard')}
              className="btn-secondary"
            >
              Back to Dashboard
            </button>
          </div>
        </div>
      </nav>

      <main className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <div className="grid lg:grid-cols-2 gap-8">
          {/* Input Form */}
          <div className="card">
            <h2 className="text-2xl font-bold mb-6">Location Analysis</h2>

            {error && (
              <div className="mb-4 p-3 bg-red-50 border border-red-200 text-red-700 rounded-lg">
                {error}
              </div>
            )}

            <form onSubmit={handleSubmit} className="space-y-4">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Address (Optional)
                </label>
                <input
                  type="text"
                  value={formData.address}
                  onChange={(e) => setFormData({ ...formData, address: e.target.value })}
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500"
                  placeholder="123 Main St, City"
                />
              </div>

              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">
                    Latitude *
                  </label>
                  <input
                    type="number"
                    step="any"
                    required
                    value={formData.latitude}
                    onChange={(e) => setFormData({ ...formData, latitude: e.target.value })}
                    className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500"
                    placeholder="10.762622"
                  />
                </div>

                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">
                    Longitude *
                  </label>
                  <input
                    type="number"
                    step="any"
                    required
                    value={formData.longitude}
                    onChange={(e) => setFormData({ ...formData, longitude: e.target.value })}
                    className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500"
                    placeholder="106.660172"
                  />
                </div>
              </div>

              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">
                    Industry *
                  </label>
                  <select
                    value={formData.industry}
                    onChange={(e) => setFormData({ ...formData, industry: e.target.value })}
                    className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500"
                  >
                    <option value="F&B">F&B</option>
                    <option value="Retail">Retail</option>
                    <option value="Service">Service</option>
                  </select>
                </div>

                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">
                    Radius (meters)
                  </label>
                  <select
                    value={formData.radius}
                    onChange={(e) => setFormData({ ...formData, radius: e.target.value })}
                    className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500"
                  >
                    <option value="300">300m</option>
                    <option value="500">500m</option>
                    <option value="1000">1000m</option>
                  </select>
                </div>
              </div>

              <div className="border-t pt-4">
                <h3 className="font-semibold mb-3">Weight Configuration</h3>
                <div className="space-y-2">
                  {[
                    { key: 'weightA', label: 'Competition & Saturation' },
                    { key: 'weightB', label: 'Traffic & Accessibility' },
                    { key: 'weightC', label: 'Socio-Economic' },
                    { key: 'weightD', label: 'Infrastructure' },
                    { key: 'weightE', label: 'Legal Risk' },
                  ].map(({ key, label }) => (
                    <div key={key} className="flex items-center gap-4">
                      <label className="text-sm text-gray-700 flex-1">{label}</label>
                      <input
                        type="number"
                        step="0.01"
                        min="0"
                        max="1"
                        value={(formData as any)[key]}
                        onChange={(e) => setFormData({ ...formData, [key]: e.target.value })}
                        className="w-20 px-2 py-1 border border-gray-300 rounded text-sm"
                      />
                    </div>
                  ))}
                </div>
              </div>

              <button
                type="submit"
                disabled={loading}
                className="w-full btn-primary disabled:opacity-50"
              >
                {loading ? 'Analyzing...' : 'Analyze Location'}
              </button>
            </form>
          </div>

          {/* Results */}
          <div className="card">
            <h2 className="text-2xl font-bold mb-6">Analysis Results</h2>

            {!result && (
              <div className="text-center py-12 text-gray-400">
                <div className="text-6xl mb-4">🎯</div>
                <p>Results will appear here</p>
              </div>
            )}

            {result && (
              <div className="space-y-6">
                {/* Risk Alert */}
                {result.riskAlert && (
                  <div className="p-4 bg-red-50 border-2 border-red-500 rounded-lg">
                    <h3 className="font-bold text-red-800 mb-2">⚠️ {result.riskAlert.level} RISK ALERT</h3>
                    <p className="text-red-700 mb-2">{result.riskAlert.message}</p>
                    {result.riskAlert.recommendations?.length > 0 && (
                      <ul className="list-disc list-inside text-sm text-red-600">
                        {result.riskAlert.recommendations.map((rec: string, i: number) => (
                          <li key={i}>{rec}</li>
                        ))}
                      </ul>
                    )}
                  </div>
                )}

                {/* Total Score */}
                <div className="text-center p-6 bg-gradient-to-br from-primary-50 to-primary-100 rounded-lg">
                  <p className="text-gray-700 mb-2">Total Score</p>
                  <p className="text-5xl font-bold text-primary-600">
                    {result.scores.total.toFixed(1)}<span className="text-2xl">/10</span>
                  </p>
                </div>

                {/* Pillar Scores */}
                <div className="space-y-3">
                  {Object.entries(result.scores.pillars).map(([key, pillar]: [string, any]) => (
                    <div key={key} className="border rounded-lg p-4">
                      <div className="flex justify-between items-center mb-2">
                        <h4 className="font-semibold">{key}. {pillar.name}</h4>
                        <span className="text-lg font-bold text-primary-600">
                          {pillar.score.toFixed(1)}/10
                        </span>
                      </div>
                      <div className="grid grid-cols-2 gap-2 text-sm">
                        {Object.entries(pillar.subScores).map(([subKey, sub]: [string, any]) => (
                          <div key={subKey} className="flex justify-between text-gray-600">
                            <span>{subKey}: {sub.name}</span>
                            <span>{sub.score.toFixed(1)}</span>
                          </div>
                        ))}
                      </div>
                    </div>
                  ))}
                </div>
              </div>
            )}
          </div>
        </div>
      </main>
    </div>
  );
}
