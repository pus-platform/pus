import * as React from "react";
import Box from "@mui/material/Box";
import Modal from "@mui/material/Modal";
import { useMediaQuery } from "@mui/material";

const modalStyle = {
  position: "absolute" as "absolute",
  top: "50%",
  left: "50%",
  transform: "translate(-50%, -50%)",
  bgcolor: "",
};
const ModStyle = {
  bgcolor: "rgba(0,0,0,.7)",
};
const ModStyleMobile = {
  bgcolor: "rgba(0,0,0,1)",
};

interface BasicModalProps {
  isOpen: boolean;
  onClose: () => void;
  children: React.ReactNode;
}

const BasicModal: React.FC<BasicModalProps> = ({
  isOpen,
  onClose,
  children,
}) => {
  const isMobile = useMediaQuery("(max-width:600px)");
  return (
    <div>
      <Modal
        open={isOpen}
        onClose={onClose}
        sx={isMobile ? ModStyleMobile : ModStyle}
        aria-labelledby="modal-modal-title"
        aria-describedby="modal-modal-description"
      >
        <Box sx={!isMobile && modalStyle}>
          <Box textAlign="center">{children}</Box>
        </Box>
      </Modal>
    </div>
  );
};

export default BasicModal;
