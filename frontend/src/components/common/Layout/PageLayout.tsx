import { FC } from "react";
import { Outlet } from "react-router-dom";
import { Flowbite } from "flowbite-react";
import Sidebar from "../../Sidebar/Sidebar";
import globalTheme from "../../../theme/theme";
import MobileBottomNav from "../../MobileBottomNav/MobileBottomNav";
import MobileTopBar from "../../MobileTopBar/MobileTopBar";

interface PageLayoutProps {}

const PageLayout: FC<PageLayoutProps> = () => {
  return (
    <Flowbite theme={{ theme: globalTheme }}>
      <div className="w-full md:flex dark:bg-black">
        <MobileTopBar avatarSrc={""} links={[]} />
        <Sidebar />
        <div className="flex flex-1 h-full overflow-y-auto max-h-screen pb-14">
          <Outlet />
        </div>
        <MobileBottomNav />
      </div>
    </Flowbite>
  );
};

export default PageLayout;
