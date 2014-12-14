package org.multibit.hd.ui.views.wizards.welcome.create_trezor_wallet;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import net.miginfocom.swing.MigLayout;
import org.multibit.hd.ui.languages.MessageKey;
import org.multibit.hd.ui.views.components.Components;
import org.multibit.hd.ui.views.components.ModelAndView;
import org.multibit.hd.ui.views.components.Panels;
import org.multibit.hd.ui.views.components.panels.PanelDecorator;
import org.multibit.hd.ui.views.components.trezor_display.TrezorDisplayModel;
import org.multibit.hd.ui.views.components.trezor_display.TrezorDisplayView;
import org.multibit.hd.ui.views.fonts.AwesomeIcon;
import org.multibit.hd.ui.views.wizards.AbstractWizard;
import org.multibit.hd.ui.views.wizards.AbstractWizardPanelView;
import org.multibit.hd.ui.views.wizards.credentials.CredentialsConfirmCipherKeyPanelModel;
import org.multibit.hd.ui.views.wizards.welcome.WelcomeWizardModel;

import javax.swing.*;

/**
 * <p>Wizard to provide the following to UI:</p>
 * <ul>
 * <li>Ask the user to press "confirm" on their Trezor in response to an Encrypt message</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */

public class CreateTrezorWalletConfirmCreateWalletPanelView extends AbstractWizardPanelView<WelcomeWizardModel, CredentialsConfirmCipherKeyPanelModel> {

  private ModelAndView<TrezorDisplayModel, TrezorDisplayView> trezorDisplayMaV;

  /**
   * @param wizard    The wizard managing the states
   * @param panelName The panel name to filter events from components
   */
  public CreateTrezorWalletConfirmCreateWalletPanelView(AbstractWizard<WelcomeWizardModel> wizard, String panelName) {

    super(wizard, panelName, MessageKey.TREZOR_PRESS_CONFIRM_TITLE, AwesomeIcon.SHIELD);

  }

  @Override
  public void newPanelModel() {

    // Bind it to the wizard model in case of failure
    getWizardModel().setTrezorConfirmCreateWalletPanelView(this);

  }

  @Override
  public void initialiseContent(JPanel contentPanel) {

    contentPanel.setLayout(new MigLayout(
      Panels.migXYLayout(),
      "[]", // Column constraints
      "[]10[]" // Row constraints
    ));

    trezorDisplayMaV = Components.newTrezorDisplayMaV(getPanelName());

    contentPanel.add(trezorDisplayMaV.getView().newComponentPanel(), "align center,wrap");

    // Register the components
    registerComponents(trezorDisplayMaV);

  }

  @Override
  protected void initialiseButtons(AbstractWizard<WelcomeWizardModel> wizard) {

    PanelDecorator.addExitCancelNext(this, wizard);

  }

  @Override
  public void afterShow() {

    SwingUtilities.invokeLater(new Runnable() {

      @Override public void run() {

        // Set the confirm text
        trezorDisplayMaV.getView().setOperationText(MessageKey.TREZOR_PRESS_CONFIRM_OPERATION);

        // Show wipe message
        trezorDisplayMaV.getView().setDisplayText(MessageKey.TREZOR_WIPE_CONFIRM_DISPLAY);

        // Reassure users that this is an unlock screen but rely on the Trezor buttons to do it
        getNextButton().setEnabled(false);

      }

    });

  }

  @Override
  public boolean beforeHide(boolean isExitCancel) {

    // Don't block an exit
    if (isExitCancel) {
      return true;
    }

    // Defer the hide operation
    return false;
  }

  @Override
  public void updateFromComponentModels(Optional componentModel) {

    // No need to update the wizard it has the references

  }

  /**
   * @return The Trezor display view to avoid method duplication
   */
  public TrezorDisplayView getTrezorDisplayView() {
    return trezorDisplayMaV.getView();
  }

  /**
   * @param visible True if the display should not be visible
   */
  public void setDisplayVisible(boolean visible) {
    this.trezorDisplayMaV.getView().setDisplayVisible(visible);
  }

  public void disableForWipe() {

    Preconditions.checkState(SwingUtilities.isEventDispatchThread(), "Must be on EDT");

    getNextButton().setEnabled(false);
    getExitButton().setEnabled(false);

    trezorDisplayMaV.getView().setSpinnerVisible(true);

  }

  public void enableForFailedWipe() {

    Preconditions.checkState(SwingUtilities.isEventDispatchThread(), "Must be on EDT");

    getNextButton().setEnabled(false);
    getExitButton().setEnabled(true);

    trezorDisplayMaV.getView().setSpinnerVisible(false);

  }

}
