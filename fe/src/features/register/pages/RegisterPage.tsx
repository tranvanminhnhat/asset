'use client';
import React from 'react';
import LoginForm from '../components/RegisterForm';

const RegisterPage: React.FC = () => {
  return (
    <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '100vh' }}>
      <LoginForm />
    </div>
  );
};

export default RegisterPage;
