package Tree.Rsatar.interfaces;


import Tree.Rsatar.dto.AbstractDTO;

public interface IDtoConvertible {
    public <T extends AbstractDTO> T toDTO();
}