"use client";

import { Provider } from "react-redux";
import { store } from "../store";
import AuthGuard from "@/common/components/AuthGuard";
import AppTopRightToolbar from "@/common/components/AppTopRightToolbar";
import { I18nProvider } from "@/common/i18n/I18nProvider";

export function Providers({ children }: { children: React.ReactNode }) {
  return (
    <Provider store={store}>
      <I18nProvider>
        <AuthGuard>{children}</AuthGuard>
        <AppTopRightToolbar />
      </I18nProvider>
    </Provider>
  );
}