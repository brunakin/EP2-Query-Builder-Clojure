(defn busca-tabela [nome]
    {:tabela nome})

(defn campos [query lista-campos]
    (assoc query :campos lista-campos))

(defn filtros [query condicoes]
    (assoc query :filtros condicoes))


(defn comp-sql [filtro]
    (if (string? filtro)
        filtro
        (if (:igual_a filtro)
            (str (:campo filtro) " = \"" (:igual_a filtro) "\"")
            (if (:maior_que filtro)
                (str (:campo filtro) " > " (:maior_que filtro))
                (if (:menor_que filtro)
                    (str (:campo filtro) " < " (:menor_que filtro))
                    (if (:em filtro)
                        (str (:campo filtro) " IN " (:em filtro))
                        (if (:valor filtro)
                            (str (:campo filtro) " = '" (:valor filtro) "'")
                              "Não tem nenhum comparador da lista")))))))


; Filtro para o E
(defn juntar-e [acc filtro]
    (if (empty? acc)
        (comp-sql filtro)
        (str acc " AND " (comp-sql filtro))))

(defn e_s [lista]
  (str "("
       (reduce juntar-e "" lista)
       ")"))

; Filtro para o OU
(defn juntar-ou [acc filtro]
    (if (empty? acc)
        (comp-sql filtro)
        (str acc " OR " (comp-sql filtro))))

(defn ou_s [lista]
  (str "("
       (reduce juntar-ou "" lista)
       ")"))


(defn gerar-sql [query]
    (let [tabela (:tabela query)
        campos (if (:campos query)
                 (clojure.string/join ", " (:campos query))
                 "*")
        where (if (:filtros query)
                (str " WHERE " (:filtros query))
                "")]
    (str "SELECT " campos " FROM " tabela where)))

; Exemplo de uso do exercócio
(def consulta
  (-> (busca-tabela "usuario")
      (campos ["abc" "xyz"])
      (filtros 
			(e_s
				[
    				{:campo 'nome', :igual_a "José"},
					{:campo 'idade', :maior_que 20},
                    {:campo 'id', :em [10, 20, 30]},
                    {:campo 'status', :igual_a true},
					(ou_s [{:campo 'camiseta', :valor 'verde'}, {:campo 'camiseta', :valor 'azul'}])
				]
			)
		)
    )
)

(println (gerar-sql consulta))



