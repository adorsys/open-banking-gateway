describe('workspace-project App', () => {

  beforeEach(() => {
    cy.spy(console, 'error').as('consoleError');
  });

  it('should have title', () => {
    cy.visit('/');
    cy.title().should('eq', 'ConsentUi');
  });

  afterEach(() => {
    cy.get('@consoleError').should('not.be.called');
  });
});
