import { Outlet, Navigate } from "react-router-dom";

import { useUserContext } from "../../context/AuthContext"; 

export default function AuthLayout() {
  const { isAuthenticated } = useUserContext();

  return (
    <>
      {isAuthenticated ? (
        <Navigate to="/" />
      ) : (
        <>
          <section className="flex flex-1 justify-center items-center flex-col py-10" style={{
            paddingTop: '0px', paddingBottom: '0px',
        }}>
            <Outlet />
          </section>
        </>
      )}
    </>
  );
}
