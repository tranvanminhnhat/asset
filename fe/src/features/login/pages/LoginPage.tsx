'use client';
import React from 'react';
import LoginForm from '../components/LoginForm';

const LoginPage: React.FC = () => {
  return (
    <div className="min-h-screen bg-[#eff8f1] px-4 py-10 flex items-center justify-center">
      <LoginForm />
    </div>
  );
};

export default LoginPage;
