import Link from 'next/link';
import { Button } from '@/components/ui/button';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { CheckCircle2, Shield, Zap, Users } from 'lucide-react';

export default function Home() {
  return (
    <div className="min-h-screen bg-gradient-to-br from-blue-50 to-indigo-100">
      <div className="container mx-auto px-4 py-16">
        {/* Hero Section */}
        <div className="text-center mb-16">
          <h1 className="text-5xl md:text-6xl font-bold mb-6 bg-gradient-to-r from-blue-600 to-indigo-600 bg-clip-text text-transparent">
            Modern Todo List
          </h1>
          <p className="text-xl text-muted-foreground mb-8 max-w-2xl mx-auto">
            A powerful, microservices-based todo application built with Spring Boot, Golang, and Next.js.
            Perfect for showcasing modern development practices in recruitment.
          </p>
          <div className="flex gap-4 justify-center">
            <Link href="/register">
              <Button size="lg" className="text-lg px-8">
                Get Started
              </Button>
            </Link>
            <Link href="/login">
              <Button size="lg" variant="outline" className="text-lg px-8">
                Login
              </Button>
            </Link>
          </div>
        </div>

        {/* Features */}
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-16">
          <Card>
            <CardHeader>
              <Shield className="h-10 w-10 mb-2 text-blue-600" />
              <CardTitle>Secure</CardTitle>
              <CardDescription>
                JWT authentication with Spring Boot and rate limiting
              </CardDescription>
            </CardHeader>
          </Card>
          <Card>
            <CardHeader>
              <Zap className="h-10 w-10 mb-2 text-yellow-600" />
              <CardTitle>Fast</CardTitle>
              <CardDescription>
                Golang-powered todo service with optimized PostgreSQL queries
              </CardDescription>
            </CardHeader>
          </Card>
          <Card>
            <CardHeader>
              <Users className="h-10 w-10 mb-2 text-green-600" />
              <CardTitle>Modern UI</CardTitle>
              <CardDescription>
                Built with Next.js 14, TypeScript, and shadcn/ui components
              </CardDescription>
            </CardHeader>
          </Card>
          <Card>
            <CardHeader>
              <CheckCircle2 className="h-10 w-10 mb-2 text-indigo-600" />
              <CardTitle>Production Ready</CardTitle>
              <CardDescription>
                Microservices architecture following best practices
              </CardDescription>
            </CardHeader>
          </Card>
        </div>

        {/* Tech Stack */}
        <Card>
          <CardHeader>
            <CardTitle className="text-2xl">Technology Stack</CardTitle>
            <CardDescription>Built with industry-leading technologies</CardDescription>
          </CardHeader>
          <CardContent>
            <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
              <div>
                <h3 className="font-semibold mb-3">Frontend</h3>
                <ul className="space-y-2 text-sm text-muted-foreground">
                  <li>• Next.js 14 with App Router</li>
                  <li>• TypeScript</li>
                  <li>• Tailwind CSS</li>
                  <li>• shadcn/ui Components</li>
                </ul>
              </div>
              <div>
                <h3 className="font-semibold mb-3">Backend</h3>
                <ul className="space-y-2 text-sm text-muted-foreground">
                  <li>• Spring Boot 3.2 (Auth Service)</li>
                  <li>• DOMA ORM</li>
                  <li>• Golang (Todo Service)</li>
                  <li>• Node.js (API Gateway)</li>
                </ul>
              </div>
              <div>
                <h3 className="font-semibold mb-3">Infrastructure</h3>
                <ul className="space-y-2 text-sm text-muted-foreground">
                  <li>• PostgreSQL Database</li>
                  <li>• Rate Limiting</li>
                  <li>• Docker & Docker Compose</li>
                  <li>• Security Best Practices</li>
                </ul>
              </div>
            </div>
          </CardContent>
        </Card>
      </div>
    </div>
  );
}

