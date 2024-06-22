import React from "react";
import ReactDOM from "react-dom/client";
import App from "./App.tsx";
import "./index.css";
import { QueryProvider } from "./context/QueryContext.tsx";
import AuthProvider from "./context/AuthContext.tsx";
import { BrowserRouter } from "react-router-dom";
import { GoogleOAuthProvider } from "@react-oauth/google";
import { MessageProvider } from "./context/MessageContext.tsx";

ReactDOM.createRoot(document.getElementById("root")!).render(
  <React.StrictMode>
    <BrowserRouter>
      <GoogleOAuthProvider clientId="604047651790-qkc0febahrdm2kfqeds52ma8bocnntd7.apps.googleusercontent.com">
        <QueryProvider>
          <AuthProvider>
            <MessageProvider>
              <App />
            </MessageProvider>
          </AuthProvider>
        </QueryProvider>
      </GoogleOAuthProvider>
    </BrowserRouter>
  </React.StrictMode>
);
