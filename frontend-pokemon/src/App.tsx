import './App.css'
import { useEffect, useMemo, useState } from 'react'

type Pokemon = {
  id: string
  nombre: string
  vida: number
  nivel: number
}

const fallbackPokemon: Pokemon[] = [
  { id: 'pikachu', nombre: 'Pikachu', vida: 72, nivel: 24 },
  { id: 'squirtle', nombre: 'Squirtle', vida: 100, nivel: 22 }
]

const spriteByName: Record<string, string> = {
  pikachu: '/img/Pikachu.png',
  squirtle: '/img/Squirtle.webp'
}

function getSprite(nombre: string): string {
  const normalizedName = nombre.trim().toLowerCase()
  return spriteByName[normalizedName] ?? '/img/Pikachu.png'
}

function getHpClass(vida: number): string {
  if (vida >= 70) {
    return 'hp-high'
  }

  if (vida >= 35) {
    return 'hp-medium'
  }

  return 'hp-low'
}

function clampHp(vida: number): number {
  return Math.max(0, Math.min(vida, 100))
}

function App() {
  const [pokemon, setPokemon] = useState<Pokemon[]>(fallbackPokemon)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)
  const [attacking, setAttacking] = useState(false)

  useEffect(() => {
    const abortController = new AbortController()

    const loadPokemon = async () => {
      try {
        setLoading(true)
        const response = await fetch('/api/pokemon', { signal: abortController.signal })

        if (!response.ok) {
          throw new Error(`Error HTTP ${response.status}`)
        }

        const data = (await response.json()) as Pokemon[]

        if (Array.isArray(data) && data.length > 0) {
          setPokemon(data)
          setError(null)
          return
        }

        setError('La API respondió sin datos.')
      } catch (err) {
        if (abortController.signal.aborted) {
          return
        }

        setError('No se pudo conectar con /api/pokemon. Mostrando datos de ejemplo.')
        console.error(err)
      } finally {
        if (!abortController.signal.aborted) {
          setLoading(false)
        }
      }
    }

    void loadPokemon()

    return () => {
      abortController.abort()
    }
  }, [])

  useEffect(() => {
    const eventSource = new EventSource('/api/pokemon/stream')

    const onSnapshot = (event: MessageEvent<string>) => {
      try {
        const data = JSON.parse(event.data) as Pokemon[]
        if (Array.isArray(data) && data.length > 0) {
          setPokemon(data)
          setError(null)
          setLoading(false)
        }
      } catch (err) {
        console.error('Error procesando snapshot SSE', err)
      }
    }

    const onPokemonUpdated = (event: MessageEvent<string>) => {
      try {
        const data = JSON.parse(event.data) as Pokemon[]
        if (Array.isArray(data)) {
          setPokemon(data)
          setError(null)
        }
      } catch (err) {
        console.error('Error procesando actualizacion SSE', err)
      }
    }

    eventSource.addEventListener('snapshot', onSnapshot as EventListener)
    eventSource.addEventListener('pokemon-updated', onPokemonUpdated as EventListener)

    eventSource.onerror = () => {
      setError('Conexion en tiempo real perdida. Reintentando...')
    }

    return () => {
      eventSource.removeEventListener('snapshot', onSnapshot as EventListener)
      eventSource.removeEventListener('pokemon-updated', onPokemonUpdated as EventListener)
      eventSource.close()
    }
  }, [])

  const [enemyPokemon, playerPokemon] = useMemo(() => {
    if (pokemon.length >= 2) {
      return [pokemon[0], pokemon[1]]
    }

    if (pokemon.length === 1) {
      return [pokemon[0], fallbackPokemon[1]]
    }

    return fallbackPokemon
  }, [pokemon])

  const attackEnemy = async () => {
    if (!enemyPokemon?.id) {
      return
    }

    try {
      setAttacking(true)
      const response = await fetch(`/api/pokemon/${enemyPokemon.id}/damage`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({ damage: 20 })
      })

      if (!response.ok) {
        throw new Error(`Error HTTP ${response.status}`)
      }
    } catch (err) {
      setError('No se pudo aplicar el ataque.')
      console.error(err)
    } finally {
      setAttacking(false)
    }
  }

  return (
    <main className="battle-screen">
      <section className="battle-arena" aria-label="Arena de batalla Pokemon">
        {loading ? <p className="status-message">Cargando pokemon...</p> : null}
        {error ? <p className="status-message status-error">{error}</p> : null}

        <article className="pokemon-card enemy">
          <div className="card-top">
            <h2>{enemyPokemon.nombre}</h2>
            <span className="level">Nv. {enemyPokemon.nivel}</span>
          </div>
          <div className="hp-row" role="img" aria-label={`Vida de ${enemyPokemon.nombre} ${enemyPokemon.vida} de 100`}>
            <span>PS</span>
            <div className={`hp-track ${getHpClass(enemyPokemon.vida)}`}>
              <progress className="hp-meter" max={100} value={clampHp(enemyPokemon.vida)} />
            </div>
          </div>
        </article>

        <div className="enemy-sprite" aria-hidden="true">
          <div className="shadow" />
          <img className="pokemon-image enemy-image" src={getSprite(enemyPokemon.nombre)} alt={enemyPokemon.nombre} />
        </div>

        <div className="player-sprite" aria-hidden="true">
          <div className="shadow" />
          <img className="pokemon-image player-image" src={getSprite(playerPokemon.nombre)} alt={playerPokemon.nombre} />
        </div>

        <article className="pokemon-card player">
          <div className="card-top">
            <h2>{playerPokemon.nombre}</h2>
            <span className="level">Nv. {playerPokemon.nivel}</span>
          </div>
          <div className="hp-row" role="img" aria-label={`Vida de ${playerPokemon.nombre} ${playerPokemon.vida} de 100`}>
            <span>PS</span>
            <div className={`hp-track ${getHpClass(playerPokemon.vida)}`}>
              <progress className="hp-meter" max={100} value={clampHp(playerPokemon.vida)} />
            </div>
          </div>
          <p className="hp-text">{playerPokemon.vida} / 100</p>
          <button className="attack-button" type="button" onClick={attackEnemy} disabled={attacking || loading}>
            {attacking ? 'Atacando...' : 'Ataque (20)'}
          </button>
        </article>
      </section>
    </main>
  )
}

export default App
