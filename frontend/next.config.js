/** @type {import('next').NextConfig} */
const nextConfig = {
  reactStrictMode: true,
  env: {
    NEXT_PUBLIC_AUTH_SERVICE_URL: process.env.NEXT_PUBLIC_AUTH_SERVICE_URL,
    NEXT_PUBLIC_CORE_SERVICE_URL: process.env.NEXT_PUBLIC_CORE_SERVICE_URL,
  },
}

module.exports = nextConfig
