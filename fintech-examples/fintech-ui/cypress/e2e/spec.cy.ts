describe('workspace-project App', () => {

  beforeEach(() => {
    cy.spy(console, 'error').as('consoleError');
  });

  it('should display welcome message', () => {
    cy.visit('/');
    cy.contains('Hello!')
    cy.get('input[formcontrolname="username"]').should('exist');
    cy.get('input[formcontrolname="password"]').should('exist');
  });

  afterEach(() => {
    cy.get('@consoleError').should('not.be.called');
  });

})
