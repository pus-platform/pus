import React, { useState } from "react";
import { Link } from "react-router-dom";
import MyAvatar from "../common/MyAvatar/MyAvatar";
import Logo from "../../assets/logo.png";
import { Search } from 'lucide-react';
interface MobileTopBarProps {
  avatarSrc: string;
  links: { label: string; to: string }[];
}

const MobileTopBar: React.FC<MobileTopBarProps> = () => {
  const [isOpen, setIsOpen] = useState(false);

  return (
    <nav className="bg-gray-100 dark:bg-[#101012] md:hidden">
      <div className="max-w-7xl mx-auto pl-4 pr-2">
        <div className="flex justify-between h-16">
          <div className="flex items-center">
            <Link to="/" className="flex items-center gap-[8px]">
              <img src={Logo} className="h-12" alt="logo" />
            </Link>
          </div>
          <div className="flex items-center">
            <MyAvatar size="xs" row />
            <button
              className="block text-gray-800 dark:text-gray-400 p-2 rounded-lg hover:text-black  dark:hover:text-white focus:outline-none bg-[#EFEFEF] dark:bg-gray-800"
              onClick={() => setIsOpen(!isOpen)}
            >
              <svg
                className="h-6 w-6"
                xmlns="http://www.w3.org/2000/svg"
                fill="none"
                viewBox="0 0 24 24"
                stroke="currentColor"
                aria-hidden="true"
              >
                {isOpen ? (
                  <path
                    strokeLinecap="round"
                    strokeLinejoin="round"
                    strokeWidth={2}
                    d="M6 18L18 6M6 6l12 12"
                  />
                ) : (
                  <path
                    d="M3 12H15M3 6H21M3 18H21"
                    stroke="#877EFF"
                    strokeWidth="2"
                    strokeLinecap="round"
                    strokeLinejoin="round"
                  />
                )}
              </svg>
            </button>
          </div>
        </div>
      </div>

      {/* Mobile menu */}
      {isOpen && (
        <div className="px-[20px] pt-[25px] pb-[49px] space-y-1 rounded-br-[10px] rounded-bl-[10px]">
          <Link
            to="/search"
            className="flex items-center gap-[10px] p-4 rounded-md text-base font-medium text-black dark:text-white hover:bg-gray-200 dark:hover:bg-gray-700 focus:outline-none focus:bg-gray-200 dark:focus:bg-gray-700"
          >
            <Search color="#877EFF" className="w-[18px] h-[18px] text-[#877EFF]" />
            <span>Search</span>
          </Link>

          <Link
            to="/notifications"
            className="flex items-center gap-[10px] p-4 rounded-md text-base font-medium text-black dark:text-white hover:bg-gray-200 dark:hover:bg-gray-700 focus:outline-none focus:bg-gray-200 dark:focus:bg-gray-700"
          >
            <svg
              width="18"
              height="18"
              viewBox="0 0 18 18"
              fill="none"
              xmlns="http://www.w3.org/2000/svg"
            >
              <path
                fillRule="evenodd"
                clipRule="evenodd"
                d="M9.00027 0.9375C5.79012 0.9375 3.18777 3.53984 3.18777 6.75V7.27808C3.18777 7.80077 3.03306 8.31176 2.74312 8.74667L1.88167 10.0388C0.881845 11.5386 1.64513 13.5771 3.38407 14.0514C3.95078 14.2059 4.52227 14.3366 5.09708 14.4436L5.0985 14.4474C5.67525 15.9863 7.21673 17.0625 9.00023 17.0625C10.7837 17.0625 12.3252 15.9863 12.9019 14.4474L12.9034 14.4436C13.4782 14.3367 14.0497 14.2059 14.6165 14.0514C16.3554 13.5771 17.1187 11.5386 16.1189 10.0388L15.2574 8.74667C14.9675 8.31176 14.8128 7.80077 14.8128 7.27808V6.75C14.8128 3.53984 12.2104 0.9375 9.00027 0.9375ZM11.5326 14.6527C9.85037 14.8537 8.15008 14.8537 6.46786 14.6527C7.00108 15.4189 7.9285 15.9375 9.00023 15.9375C10.0719 15.9375 10.9994 15.4189 11.5326 14.6527ZM4.31277 6.75C4.31277 4.16117 6.41144 2.0625 9.00027 2.0625C11.5891 2.0625 13.6878 4.16117 13.6878 6.75V7.27808C13.6878 8.02287 13.9082 8.751 14.3214 9.37071L15.1828 10.6629C15.7567 11.5237 15.3186 12.6938 14.3205 12.966C10.8373 13.916 7.16327 13.916 3.68008 12.966C2.68195 12.6938 2.24384 11.5237 2.81772 10.6629L3.67918 9.37071C4.09231 8.751 4.31277 8.02287 4.31277 7.27808V6.75Z"
                fill="#877EFF"
              />
            </svg>
            <span>Notification</span>
          </Link>
          <div className="w-full flex items-center justify-center p-[16px]">
            <button className="w-full flex items-center justify-center gap-[10px] p-[10px] rounded-lg bg-[#FF5A5A] text-white hover:opacity-90">
              <svg
                width="16"
                height="16"
                viewBox="0 0 16 16"
                fill="none"
                xmlns="http://www.w3.org/2000/svg"
              >
                <path
                  d="M3.64645 8.35353C3.45119 8.15827 3.45119 7.84169 3.64645 7.64643L4.97978 6.31309C5.17504 6.11783 5.49163 6.11783 5.68689 6.31309C5.88215 6.50836 5.88215 6.82494 5.68689 7.0202L5.20711 7.49998L10 7.49998C10.2761 7.49998 10.5 7.72384 10.5 7.99998C10.5 8.27612 10.2761 8.49998 10 8.49998L5.20711 8.49998L5.68689 8.97976C5.88215 9.17502 5.88215 9.49161 5.68689 9.68687C5.49163 9.88213 5.17504 9.88213 4.97978 9.68687L3.64645 8.35353Z"
                  fill="white"
                />
                <path
                  fill-rule="evenodd"
                  clip-rule="evenodd"
                  d="M9.2969 0.833313H10.0367C10.9485 0.8333 11.6833 0.833289 12.2613 0.910996C12.8614 0.991674 13.3666 1.16427 13.7679 1.56555C14.1692 1.96682 14.3418 2.47207 14.4225 3.07215C14.5002 3.65013 14.5002 4.38501 14.5001 5.29673V10.7032C14.5002 11.615 14.5002 12.3498 14.4225 12.9278C14.3418 13.5279 14.1692 14.0331 13.7679 14.4344C13.3666 14.8357 12.8614 15.0083 12.2613 15.089C11.6833 15.1667 10.9485 15.1667 10.0367 15.1666H9.2969C8.38517 15.1667 7.65029 15.1667 7.07231 15.089C6.47224 15.0083 5.96699 14.8357 5.56571 14.4344C5.29975 14.1685 5.13379 13.8562 5.0287 13.4999C4.39445 13.499 3.86604 13.4921 3.43792 13.4345C2.92862 13.366 2.48707 13.2178 2.1346 12.8654C1.78214 12.5129 1.63395 12.0714 1.56548 11.5621C1.49997 11.0748 1.49998 10.4577 1.5 9.70246V6.29752C1.49998 5.54231 1.49997 4.92514 1.56548 4.4379C1.63395 3.9286 1.78214 3.48704 2.1346 3.13458C2.48707 2.78212 2.92862 2.63393 3.43792 2.56546C3.86604 2.5079 4.39445 2.50093 5.0287 2.50009C5.13379 2.14372 5.29975 1.83151 5.56571 1.56555C5.96699 1.16427 6.47224 0.991674 7.07231 0.910996C7.65029 0.833289 8.38517 0.8333 9.2969 0.833313ZM4.83464 11.3361C4.83704 11.7658 4.84413 12.1528 4.86856 12.4994C4.31224 12.4972 3.89867 12.4875 3.57117 12.4434C3.17276 12.3899 2.9774 12.294 2.84171 12.1583C2.70602 12.0226 2.61013 11.8272 2.55656 11.4288C2.50106 11.016 2.5 10.4665 2.5 9.66665V6.33331C2.5 5.53351 2.50106 4.98395 2.55656 4.57115C2.61013 4.17274 2.70602 3.97738 2.84171 3.84169C2.9774 3.706 3.17276 3.61011 3.57117 3.55654C3.89867 3.51251 4.31224 3.50274 4.86856 3.50059C4.84413 3.84713 4.83704 4.23417 4.83464 4.66386C4.83311 4.94 5.05572 5.1651 5.33185 5.16664C5.60799 5.16818 5.83309 4.94557 5.83463 4.66943C5.83869 3.94043 5.85762 3.42371 5.92965 3.03154C5.99906 2.65367 6.11052 2.43496 6.27282 2.27265C6.45733 2.08815 6.71638 1.96785 7.20556 1.90208C7.70913 1.83438 8.37653 1.83331 9.33348 1.83331H10.0001C10.9571 1.83331 11.6245 1.83438 12.1281 1.90208C12.6172 1.96785 12.8763 2.08815 13.0608 2.27265C13.2453 2.45716 13.3656 2.71621 13.4314 3.20539C13.4991 3.70896 13.5001 4.37637 13.5001 5.33331V10.6666C13.5001 11.6236 13.4991 12.291 13.4314 12.7946C13.3656 13.2838 13.2453 13.5428 13.0608 13.7273C12.8763 13.9118 12.6172 14.0321 12.1281 14.0979C11.6245 14.1656 10.9571 14.1666 10.0001 14.1666H9.33348C8.37653 14.1666 7.70913 14.1656 7.20556 14.0979C6.71638 14.0321 6.45733 13.9118 6.27282 13.7273C6.11052 13.565 5.99906 13.3463 5.92965 12.9684C5.85762 12.5763 5.83869 12.0595 5.83463 11.3305C5.83309 11.0544 5.60799 10.8318 5.33185 10.8333C5.05572 10.8349 4.83311 11.06 4.83464 11.3361Z"
                  fill="white"
                />
              </svg>
              <span className="text-[14px] font-[600]">Logout</span>
            </button>
          </div>
        </div>
      )}
    </nav>
  );
};

export default MobileTopBar;
