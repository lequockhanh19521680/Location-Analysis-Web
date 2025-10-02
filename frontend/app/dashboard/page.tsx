'use client';

import { useSession } from 'next-auth/react';
import { useRouter } from 'next/navigation';
import { useEffect } from 'react';

export default function DashboardPage() {
  const { data: session, status } = useSession();
  const router = useRouter();

  useEffect(() => {
    if (status === 'unauthenticated') {
      router.push('/login');
    }
  }, [status, router]);

  if (status === 'loading') {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <div className="text-center">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary-600 mx-auto"></div>
          <p className="mt-4 text-gray-600">Loading...</p>
        </div>
      </div>
    );
  }

  if (!session) {
    return null;
  }

  return (
    <div className="min-h-screen bg-gray-50">
      <nav className="bg-white shadow-sm">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex justify-between h-16 items-center">
            <h1 className="text-xl font-bold text-gray-900">
              Location Score AI
            </h1>
            <div className="flex items-center space-x-4">
              <span className="text-gray-700">Welcome, {session.user?.name || session.user?.email}</span>
              <button
                onClick={() => router.push('/analyze')}
                className="btn-primary"
              >
                New Analysis
              </button>
            </div>
          </div>
        </div>
      </nav>

      <main className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <div className="grid md:grid-cols-3 gap-6 mb-8">
          <div className="card">
            <h3 className="text-lg font-semibold mb-2">Total Analyses</h3>
            <p className="text-3xl font-bold text-primary-600">0</p>
            <p className="text-sm text-gray-600 mt-1">No analyses yet</p>
          </div>

          <div className="card">
            <h3 className="text-lg font-semibold mb-2">Average Score</h3>
            <p className="text-3xl font-bold text-primary-600">-</p>
            <p className="text-sm text-gray-600 mt-1">Run your first analysis</p>
          </div>

          <div className="card">
            <h3 className="text-lg font-semibold mb-2">Risk Alerts</h3>
            <p className="text-3xl font-bold text-red-600">0</p>
            <p className="text-sm text-gray-600 mt-1">No critical risks</p>
          </div>
        </div>

        <div className="card">
          <h2 className="text-2xl font-bold mb-6">Recent Analyses</h2>
          <div className="text-center py-12">
            <div className="text-gray-400 text-6xl mb-4">📊</div>
            <h3 className="text-xl font-semibold text-gray-700 mb-2">
              No analyses yet
            </h3>
            <p className="text-gray-600 mb-6">
              Start by analyzing your first location
            </p>
            <button
              onClick={() => router.push('/analyze')}
              className="btn-primary"
            >
              Analyze Location
            </button>
          </div>
        </div>
      </main>
    </div>
  );
}
