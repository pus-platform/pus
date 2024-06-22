import Heading from "../../components/Heading/Heading";
import { SlidersHorizontal } from 'lucide-react';
import PostList from "../../components/PostList/PostList";
import { useGetSavedPosts } from "@/lib/react-query/queries/bookmark";
import { Loader, LoaderCircle } from "lucide-react";
import { Bookmark } from 'lucide-react';


const Saved = () => {
  const { data: bookmarks, isPending } = useGetSavedPosts();

  if (isPending)
    return (
      <LoaderCircle className="animate-spin" />
    )

  return (
    <div className="dark:text-white h-full w-full px-2 md:px-8 pb-40">
      <Heading
        icon={<Bookmark className="h-8 w-8" />}
        label="Saved Posts"
      />

      <div className="flex flex-col gap-4 sm:gap-[32px] md:gap-[40px] w-full">
        <div className="flex items-center justify-between">
          <div className="flex">
            <button className="flex items-center justify-center gap-[10px] py-[12px] px-[12px] md:px-[50px] border dark:border-[#101012] dark:bg-[#101012] bg-gray-100 dark:text-white rounded-tl-[10px] rounded-bl-[10px]">
              <svg
                width="20"
                height="20"
                viewBox="0 0 20 20"
                fill="none"
                xmlns="http://www.w3.org/2000/svg"
              >
                <g clipPath="url(#clip0_1_1450)">
                  <path
                    d="M1.66675 9.99996C1.66675 6.07159 1.66675 4.1074 2.88714 2.88701C4.10752 1.66663 6.07171 1.66663 10.0001 1.66663C13.9285 1.66663 15.8926 1.66663 17.113 2.88701C18.3334 4.1074 18.3334 6.07159 18.3334 9.99996C18.3334 13.9283 18.3334 15.8925 17.113 17.1129C15.8926 18.3333 13.9285 18.3333 10.0001 18.3333C6.07171 18.3333 4.10752 18.3333 2.88714 17.1129C1.66675 15.8925 1.66675 13.9283 1.66675 9.99996Z"
                    stroke="#877EFF"
                    strokeWidth="1.5"
                  />
                  <circle
                    cx="13.3334"
                    cy="6.66667"
                    r="1.66667"
                    stroke="#877EFF"
                    strokeWidth="1.5"
                  />
                  <path
                    d="M1.66675 10.4168L3.1264 9.13963C3.8858 8.47517 5.03031 8.51328 5.74382 9.22679L9.31859 12.8016C9.89128 13.3743 10.7928 13.4523 11.4554 12.9866L11.7039 12.812C12.6574 12.1419 13.9475 12.2195 14.8138 12.9992L17.5001 15.4168"
                    stroke="#877EFF"
                    strokeWidth="1.5"
                    strokeLinecap="round"
                  />
                </g>
                <defs>
                  <clipPath id="clip0_1_1450">
                    <rect width="20" height="20" fill="white" />
                  </clipPath>
                </defs>
              </svg>
              <span className="text-[16px] font-[500] leading-[140%]">
                Posts
              </span>
            </button>
          </div>

          <button className="flex items-center justify-center gap-[10px] py-[12px] px-[16px] rounded-[14px] bg-gray-100 dark:bg-[#101012] border border-gray-100 dark:border-[#101012]">
            <span className="text-[12px] lg:text-[16px] font-[500]">All</span>
            <SlidersHorizontal color="#877EFF" className="w-[16px] h-[16px] lg:w-[20px] lg:h-[20px]" />
          </button>
        </div>

        <div className="pb-8 flex justify-center">
          <PostList posts={bookmarks?.map((b) => b.post)} />
        </div>
      </div>
    </div>
  );
};

export default Saved;
