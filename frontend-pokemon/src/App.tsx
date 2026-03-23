import './App.css'

function App() {
  return (
    <main className="battle-screen">
      <section className="battle-arena" aria-label="Arena de batalla Pokemon">
        <article className="pokemon-card enemy">
          <div className="card-top">
            <h2>Pikachu</h2>
            <span className="level">Nv. 24</span>
          </div>
          <div className="hp-row" role="img" aria-label="Vida de Pikachu 72 de 100">
            <span>PS</span>
            <div className="hp-track">
              <div className="hp-fill hp-medium" style={{ width: '72%' }} />
            </div>
          </div>
        </article>

        <div className="enemy-sprite" aria-hidden="true">
          <div className="shadow" />
          <img className="pokemon-image enemy-image" src="/img/Pikachu.png" alt="Pikachu" />
        </div>

        <div className="player-sprite" aria-hidden="true">
          <div className="shadow" />
          <img className="pokemon-image player-image" src="/img/Squirtle.webp" alt="Squirtle" />
        </div>

        <article className="pokemon-card player">
          <div className="card-top">
            <h2>Squirtle</h2>
            <span className="level">Nv. 22</span>
          </div>
          <div className="hp-row" role="img" aria-label="Vida de Squirtle 100 de 100">
            <span>PS</span>
            <div className="hp-track">
              <div className="hp-fill hp-high" style={{ width: '100%' }} />
            </div>
          </div>
          <p className="hp-text">100 / 100</p>
        </article>
      </section>
    </main>
  )
}

export default App
