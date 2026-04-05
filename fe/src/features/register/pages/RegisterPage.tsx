'use client';
import React from 'react';
import RegisterForm from '../components/RegisterForm';

const RegisterPage: React.FC = () => {
  return (
    <div className="min-h-screen bg-[#eff8f1] px-4 py-10 flex items-center justify-center">
      <RegisterForm />
    </div>
  );
};

export default RegisterPage;
