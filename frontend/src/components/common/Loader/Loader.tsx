import { LoaderCircle } from "lucide-react";

const Loader = (): React.ReactNode => (
    <div className="h-full w-full flex justify-center items-center">
        <LoaderCircle className="w-full h-full max-h-12 max-w-12 animate-spin" />
    </div>
);

export default Loader;
