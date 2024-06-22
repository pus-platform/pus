import { SlidersHorizontal } from 'lucide-react';

const Heading: React.FC<{ label: string, icon?: React.ReactNode, isFilter?: boolean }> = ({ label, icon, isFilter = false }) => {
  return (
    <div className="flex items-center py-4 sm:py-[32px] md:py-[40px] space-x-4 text-black dark:text-white">
      {icon}
      <div className="w-full flex items-center justify-between">
        <h1 className="text-lg lg:text-[30px] font-bold ">{label}</h1>{" "}
        {isFilter && (
          <button className="flex items-center justify-center gap-[10px] py-[12px] px-[16px] rounded-[14px] bg-gray-100 dark:bg-[#101012] border border-gra-100 dark:border-[#101012]">
            <span className="text-[12px] lg:text-[16px] font-[500]">All</span>
            <SlidersHorizontal color="#877EFF" className="w-[16px] h-[16px] lg:w-[20px] lg:h-[20px]" />
          </button>
        )}
      </div>
    </div>
  );
};

export default Heading;
