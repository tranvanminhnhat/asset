"use client";

import { Provider } from "react-redux";
import { store } from "../store";
import AuthGuard from "@/common/components/AuthGuard";

export function Providers({ children }: { children: React.ReactNode }) {
  return (
    <Provider store={store}>
      <AuthGuard>{children}</AuthGuard>
    </Provider>
  );
}