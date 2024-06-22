import { Link, useLocation } from "react-router-dom";
import HomeImg from "../../assets/icons/home.svg";
import SavedImg from "../../assets/icons/bookmark.svg";
import CreatePostImg from "../../assets/icons/gallery-add.svg";
import ChatImg from "../../assets/icons/chat.svg";
import PeapleImg from "../../assets/icons/people.svg";
const MobileBottomNavLinks = [
  {
    imgURL: HomeImg,
    route: "/",
    label: "Home",
  },
  {
    imgURL: SavedImg,
    route: "/saved",
    label: "Saved",
  },
  {
    imgURL: PeapleImg,
    route: "/community",
    label: "Community",
  },
  {
    imgURL: CreatePostImg,
    route: "/create-post",
    label: "Create",
  },
  {
    imgURL: ChatImg,
    route: "/chats",
    label: "Chats",
  },
];
const MobileBottomNav = () => {
  const { pathname } = useLocation();

  return (
    <section className="z-50 dark:text-white flex justify-between w-full sticky bottom-0 rounded-t-[20px] bg-gray-100 shadow-sm dark:bg-[#101012]  px-5 py-4 md:hidden ">
      {MobileBottomNavLinks.map((link) => {
        const isActive = pathname === link.route;
        return (
          <Link
            key={`${link.label}`}
            to={link.route}
            className={`${
              isActive && "rounded-[10px] bg-purple-500"
            } flex justify-center items-center flex-col gap-1 p-2 transition `}
          >
            <img
              src={link.imgURL}
              alt={link.label}
              width={16}
              height={16}
              className={`${isActive && "invert brightness-0 transition"}`}
            />

            <p className="text-[10px] text-center font-medium leading-[140%] text-light-2">
              {link.label}
            </p>
          </Link>
        );
      })}
    </section>
  );
};

export default MobileBottomNav;
